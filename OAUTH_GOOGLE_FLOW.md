# OAuth2 Google Login Flow - Hướng dẫn Frontend

## Tổng quan

Hệ thống sử dụng OAuth2 với Google để đăng nhập. Backend tự động tạo account nếu chưa tồn tại và trả về JWT token.

## Luồng OAuth2

### 1. Backend Configuration

**AccountService (Port 8081)**
- OAuth2 endpoint: `/oauth2/authorization/google`
- Callback endpoint: `/login/oauth2/code/google`
- Redirect URI trong Google Console: `http://localhost:8081/login/oauth2/code/google`

**API Gateway (Port 8888)**
- Route: `/api/auth/**` → `http://localhost:8081`
- OAuth2 routes:
  - Authorization: `http://localhost:8888/oauth2/authorization/google`
  - Callback: `http://localhost:8888/login/oauth2/code/google` (Google redirects to backend directly)

### 2. Luồng xử lý

```
Frontend → Click "Login with Google"
    ↓
Redirect to: http://localhost:8888/oauth2/authorization/google
    ↓
Google OAuth Consent Screen
    ↓
User approves
    ↓
Google redirects to: http://localhost:8081/login/oauth2/code/google
    (Note: Google redirects directly to backend, not through gateway)
    ↓
Backend processes OAuth:
    1. Lấy email, name từ Google
    2. Tạo hoặc lấy account (processOAuth2Login)
    3. Generate JWT token
    ↓
Backend redirects to: http://localhost:5173/#oauth-callback?token={JWT_TOKEN}
    ↓
Frontend xử lý callback
```

## Frontend Implementation

### Bước 1: Tạo nút "Login with Google"

```javascript
// Component hoặc page login
const handleGoogleLogin = () => {
  // Redirect đến OAuth2 endpoint qua API Gateway
  window.location.href = 'http://localhost:8888/oauth2/authorization/google';
};
```

### Bước 2: Xử lý OAuth Callback

Frontend cần xử lý route `/#oauth-callback` để nhận token từ URL:

```javascript
// Router setup (Vue Router / React Router)
// Route: /oauth-callback

// Vue Router example
{
  path: '/oauth-callback',
  component: OAuthCallback
}

// React Router example
<Route path="/oauth-callback" element={<OAuthCallback />} />
```

### Bước 3: Component OAuth Callback

```javascript
// OAuthCallback.vue (Vue) hoặc OAuthCallback.jsx (React)

import { useEffect } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
// hoặc cho Vue: import { useRouter, useRoute } from 'vue-router'

function OAuthCallback() {
  const navigate = useNavigate();
  const location = useLocation();
  
  useEffect(() => {
    // Lấy token từ URL hash
    const hash = location.hash; // Format: #oauth-callback?token=xxx
    const params = new URLSearchParams(hash.split('?')[1]);
    const token = params.get('token');
    
    if (token) {
      // Lưu token vào localStorage hoặc state management
      localStorage.setItem('token', token);
      
      // Redirect đến trang chủ hoặc dashboard
      navigate('/');
      
      // Optional: Reload để update auth state
      window.location.reload();
    } else {
      // Kiểm tra error từ URL
      const error = new URLSearchParams(location.search).get('error');
      if (error) {
        console.error('OAuth error:', error);
        navigate('/login?error=' + error);
      } else {
        navigate('/login');
      }
    }
  }, [location, navigate]);
  
  return (
    <div>
      <p>Đang xử lý đăng nhập...</p>
    </div>
  );
}
```

### Bước 4: Xử lý Error Cases

Backend có thể redirect với error:

```
http://localhost:5173/login?error=oauth_failed&message=...
http://localhost:5173/login?error=email_not_found
```

```javascript
// Login component
useEffect(() => {
  const urlParams = new URLSearchParams(window.location.search);
  const error = urlParams.get('error');
  const message = urlParams.get('message');
  
  if (error) {
    // Hiển thị error message
    setErrorMessage(message || 'Đăng nhập thất bại');
    // Clear URL params
    window.history.replaceState({}, document.title, '/login');
  }
}, []);
```

## API Endpoints

### 1. Bắt đầu OAuth Flow
```
GET http://localhost:8888/oauth2/authorization/google
```
**Response:** Redirect đến Google OAuth

**Lưu ý:** Endpoint này không có prefix `/api/auth` vì Spring Security OAuth2 tạo endpoint global `/oauth2/authorization/{registrationId}`

### 2. OAuth Callback (Backend tự xử lý)
```
GET http://localhost:8081/login/oauth2/code/google?code=xxx&state=xxx
```
**Response:** Redirect đến frontend với token

### 3. Lấy thông tin user (sau khi có token)
```
GET http://localhost:8888/api/auth/me
Headers: Authorization: Bearer {token}
```

## Lưu ý quan trọng

### 1. Redirect URI Configuration

**Vấn đề:** OAuth2 redirect URI trong `application.properties` là:
```
spring.security.oauth2.client.registration.google.redirect-uri=http://localhost:8081/login/oauth2/code/google
```

**Khi qua API Gateway:**
- Frontend gọi: `http://localhost:8888/api/auth/oauth2/authorization/google`
- Google redirect về: `http://localhost:8081/login/oauth2/code/google` (theo config)
- Backend xử lý và redirect về frontend: `http://localhost:5173/#oauth-callback?token=xxx`

**Giải pháp:** Giữ nguyên redirect URI là `http://localhost:8081/login/oauth2/code/google` vì:
- Google OAuth phải redirect về đúng URL đã đăng ký trong Google Console
- Backend xử lý callback và tự redirect về frontend

### 2. CORS Configuration

Frontend URL đã được config trong `SecurityConfig.java`:
```java
@Value("${app.frontend.url:http://localhost:5173}")
private String frontendUrl;
```

Đảm bảo `application.properties` có:
```properties
app.frontend.url=http://localhost:5173
```

### 3. Account Creation

- Nếu email chưa tồn tại: Tự động tạo account mới với:
  - Username: phần trước @ của email
  - Full name: từ Google profile
  - Role: USER (mặc định)
  - Active: true (Google đã verify email)
  - Password: random (không dùng được, chỉ dùng OAuth)

- Nếu email đã tồn tại: Sử dụng account hiện có

### 4. JWT Token

Token được generate với:
- Subject: email
- Authorities: ROLE_USER hoặc ROLE từ account
- Expiration: 24 giờ (theo `app.jwt.expiration-ms`)

## Testing

### Test OAuth Flow

1. **Start services:**
   ```bash
   # AccountService (port 8081)
   # API Gateway (port 8888)
   ```

2. **Test endpoint:**
   ```
   http://localhost:8888/api/auth/oauth2/authorization/google
   ```

3. **Expected flow:**
   - Redirect đến Google
   - Chọn account Google
   - Redirect về frontend với token trong URL

### Test với Postman/curl

Không thể test OAuth flow hoàn chỉnh với Postman vì cần browser redirect. 
Có thể test endpoint `/api/auth/google` để xem thông tin:

```bash
curl http://localhost:8888/api/auth/google
```

## Troubleshooting

### Lỗi: "redirect_uri_mismatch"
- Kiểm tra redirect URI trong Google Console phải khớp với `application.properties`
- Phải là: `http://localhost:8081/login/oauth2/code/google`

### Lỗi: CORS khi redirect
- Đảm bảo frontend URL đúng trong `app.frontend.url`
- Kiểm tra CORS config trong `SecurityConfig.java`

### Token không được lưu
- Kiểm tra frontend có xử lý `#oauth-callback` route không
- Kiểm tra localStorage/sessionStorage có được set không

### Account không được tạo
- Kiểm tra logs của AccountService
- Kiểm tra database connection
- Kiểm tra `processOAuth2Login` method

