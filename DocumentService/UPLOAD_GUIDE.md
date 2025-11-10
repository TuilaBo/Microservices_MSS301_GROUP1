# 📚 Hướng dẫn Upload Tài liệu Văn học Việt Nam - DocumentService

## 🎯 Giới thiệu
DocumentService chuyên quản lý tài liệu **Văn học Việt Nam** từ lớp 9-12, bao gồm giáo trình, bài tập, video bài giảng, và audio ngâm thơ.

## 🚀 3 Cách Upload Tài liệu

### 1. 🌐 Upload qua Web Form (Dễ nhất - Khuyên dùng)
Mở browser và truy cập:
```
http://localhost:8084/api/documents/upload-form
```

**✨ Tính năng nổi bật:**
- 🎨 Giao diện đẹp, thân thiện người dùng
- 🤖 Auto-detect loại file (PDF → Giáo trình, MP4 → Video, v.v.)
- 📊 Progress bar real-time khi upload
- ✅ Validate file size, định dạng tự động
- 👀 Preview thông tin sau upload thành công
- 📝 Auto-fill title từ tên file

**🖱️ Cách sử dụng:**
1. Drag & drop file hoặc click "Chọn file"
2. Điền thông tin: Tiêu đề, mô tả, lớp, danh mục
3. Click "🚀 Upload Tài liệu"
4. Đợi progress bar hoàn thành
5. Xem kết quả với ID tài liệu được tạo

### 2. 📤 Upload Simple API (Nhanh chóng)
Dành cho test API hoặc script automation:

```bash
curl -X POST "http://localhost:8084/api/documents/upload-simple" \
  -F "file=@truyen_kieu_phan_tich.pdf" \
  -F "title=Truyện Kiều - Phân tích toàn diện" \
  -F "description=Phân tích chi tiết tác phẩm Truyện Kiều của Nguyễn Du" \
  -F "category=Giáo trình" \
  -F "gradeLevel=11" \
  -F "subject=Ngữ văn" \
  -F "uploadedBy=gv_van" \
  -F "isPublic=true"
```

**📋 Parameters:**
- `file`: File tài liệu (required)
- `title`: Tiêu đề tài liệu (required)  
- `description`: Mô tả chi tiết (optional)
- `category`: Giáo trình | Bài tập | Video | Audio | Đề kiểm tra
- `gradeLevel`: 9 | 10 | 11 | 12
- `subject`: Ngữ văn (default)
- `uploadedBy`: ID người upload (default: guest_user)
- `isPublic`: true | false (default: true)

### 3. 🔧 Upload Advanced API (Production)
Dành cho ứng dụng frontend với JWT auth:

```bash
curl -X POST "http://localhost:8084/api/documents" \
  -H "Authorization: Bearer YOUR_JWT_TOKEN" \
  -H "Content-Type: multipart/form-data" \
  -F 'file=@ho_xuan_huong_tho_ca.pdf' \
  -F 'document={
    "title": "Hồ Xuân Hương - Thi ca và nhân cách",
    "description": "Nghiên cứu toàn diện về thơ Hồ Xuân Hương và tinh thần phản kháng",
    "category": "Giáo trình",
    "gradeLevel": 11,
    "subject": "Ngữ văn",
    "tags": "Hồ Xuân Hương,thơ,phản kháng,nữ quyền,văn học hiện thực",
    "isPublic": true
  }'
```

### 4. 📦 Bulk Upload (Upload hàng loạt)
Upload nhiều file cùng lúc:

```bash
curl -X POST "http://localhost:8084/api/documents/bulk-upload" \
  -F "files=@ca_dao_lop9.pdf" \
  -F "files=@tuc_ngu_lop9.pdf" \
  -F "files=@video_ca_dao.mp4" \
  -F "category=Giáo trình" \
  -F "gradeLevel=9" \
  -F "subject=Ngữ văn" \
  -F "uploadedBy=gv_van_bulk"
```

## 📁 Định dạng file và quy tắc

### 📖 Loại tài liệu được hỗ trợ:

| 📂 Category | 📄 File Types | 🎯 Mục đích | 📏 Max Size |
|-------------|---------------|-------------|-------------|
| **📚 Giáo trình** | PDF, DOCX | Lý thuyết, phân tích tác phẩm | 50MB |
| **📝 Bài tập** | PDF, DOCX | Bài tập, đề cương | 20MB |
| **🎬 Video** | MP4, AVI, MOV | Video bài giảng, phân tích | 500MB |
| **🔊 Audio** | MP3, WAV | Ngâm thơ, đọc tác phẩm | 100MB |
| **📋 Đề kiểm tra** | PDF | Đề thi, đề kiểm tra | 10MB |
| **🖼️ Hình ảnh** | JPG, PNG, GIF | Thumbnail, illustrations | 5MB |

### 🎭 Nội dung theo lớp học:

#### 📖 Lớp 9 - Văn học Dân gian
```
✅ Ca dao, tục ngữ Việt Nam
✅ Thần thoại và truyền thuyết
✅ Nghệ thuật ngôn ngữ dân gian
✅ Bài tập phân tích ca dao
```

#### 📖 Lớp 10 - Văn học Trung đại  
```
✅ Nguyễn Trãi - Bình Ngô đại cáo
✅ Nguyễn Bỉnh Khiêm - Triết lý Nho giáo
✅ Chữ Nôm và văn học cổ
✅ Văn xuôi thời Lý-Trần-Lê
```

#### 📖 Lớp 11 - Văn học Hiện thực XIX
```
✅ Nguyễn Du - Truyện Kiều  
✅ Hồ Xuân Hương - Thơ phản kháng
✅ Nguyễn Đình Chiểu - Lục Vân Tiên
✅ Phong trào văn học hiện thực
```

#### 📖 Lớp 12 - Văn học Hiện đại
```
✅ Thơ mới: Xuân Diệu, Huy Cận, Tế Hanh
✅ Hồ Chí Minh - Văn học và báo chí
✅ Tố Hữu - Thơ ca cách mạng
✅ Văn học miền Nam trước 1975
```

## 🏷️ Quy tắc đặt tên và tags

### 📝 Naming Convention:
```
{tac_gia}_{tac_pham}_{lop}.{ext}
Ví dụ: nguyen_du_truyen_kieu_11.pdf
       ho_xuan_huong_tho_ca_11.pdf
       to_huu_tho_cach_mang_12.pdf
```

### 🏷️ Tags Suggestions:
```
🎭 Tác giả: "Nguyễn Du", "Hồ Xuân Hương", "Tố Hữu"
📖 Tác phẩm: "Truyện Kiều", "Bình Ngô đại cáo"
🎨 Thể loại: "thơ", "văn xuôi", "truyện", "bài ca"
⏰ Thời kỳ: "dân gian", "trung đại", "hiện thực", "hiện đại"
🎯 Chủ đề: "tình yêu", "số phận", "phản kháng", "cách mạng"
```

## ✅ Response Format

### 🎉 Upload Success:
```json
{
  "status": 201,
  "message": "Upload tài liệu thành công",
  "data": {
    "id": "64f123abc456789012345678",
    "title": "Truyện Kiều - Phân tích toàn diện",
    "fileName": "truyen_kieu_phan_tich.pdf",
    "fileType": "PDF",
    "category": "Giáo trình",
    "gradeLevel": 11,
    "subject": "Ngữ văn",
    "fileSize": 2048576,
    "uploadedBy": "gv_van",
    "createdAt": "2024-11-10T14:30:00Z"
  }
}
```

### ❌ Upload Error:
```json
{
  "status": 400,
  "message": "Lỗi upload tài liệu",
  "error": "File size exceeds maximum allowed (100MB)"
}
```

## 🔍 Test & Debug

### 🧪 Test Upload:
```bash
# 1. Check service health
curl http://localhost:8084/api/documents/health

# 2. Test simple upload
curl -X POST "http://localhost:8084/api/debug/test-save"

# 3. Check documents count  
curl http://localhost:8084/api/debug/count

# 4. View all documents
curl http://localhost:8084/api/documents
```

### 🛠️ Debug Commands:
```bash
# Clear all documents
curl -X DELETE http://localhost:8084/api/debug/clear

# Force init sample data
curl -X POST http://localhost:8084/api/debug/force-init

# View raw documents
curl http://localhost:8084/api/debug/all
```

## 📊 Upload Examples by Content Type

### 📚 Giáo trình Upload:
```bash
curl -X POST "http://localhost:8084/api/documents/upload-simple" \
  -F "file=@van_hoc_dan_gian_9.pdf" \
  -F "title=Văn học dân gian Việt Nam" \
  -F "description=Tổng quan về ca dao, tục ngữ, thần thoại Việt Nam" \
  -F "category=Giáo trình" \
  -F "gradeLevel=9" \
  -F "subject=Ngữ văn"
```

### 🎬 Video Upload:
```bash
curl -X POST "http://localhost:8084/api/documents/upload-simple" \
  -F "file=@video_phan_tich_truyen_kieu.mp4" \
  -F "title=Video: Phân tích Truyện Kiều" \
  -F "description=Video bài giảng phân tích tác phẩm Truyện Kiều" \
  -F "category=Video" \
  -F "gradeLevel=11" \
  -F "subject=Ngữ văn"
```

### 🔊 Audio Upload:
```bash
curl -X POST "http://localhost:8084/api/documents/upload-simple" \
  -F "file=@ngam_tho_ho_xuan_huong.mp3" \
  -F "title=Audio: Ngâm thơ Hồ Xuân Hương" \
  -F "description=Bản ngâm thơ Hồ Xuân Hương với giọng đọc truyền cảm" \
  -F "category=Audio" \
  -F "gradeLevel=11" \
  -F "subject=Ngữ văn"
```

### 📝 Bài tập Upload:
```bash
curl -X POST "http://localhost:8084/api/documents/upload-simple" \
  -F "file=@bai_tap_phan_tich_thuy_kieu.docx" \
  -F "title=Bài tập phân tích nhân vật Thúy Kiều" \
  -F "description=Bài tập chi tiết về hình tượng nhân vật Thúy Kiều" \
  -F "category=Bài tập" \
  -F "gradeLevel=11" \
  -F "subject=Ngữ văn"
```

## 🚨 Troubleshooting

### ❗ Lỗi thường gặp:

1. **File quá lớn:**
   ```
   Error: File size exceeds maximum allowed
   ➡️ Solution: Compress file hoặc split thành parts nhỏ hơn
   ```

2. **Format không hỗ trợ:**
   ```
   Error: Unsupported file format
   ➡️ Solution: Convert sang PDF, DOCX, MP4, MP3
   ```

3. **MongoDB connection:**
   ```
   Error: Could not save document  
   ➡️ Solution: Check MongoDB service: sudo systemctl start mongod
   ```

4. **Upload directory:**
   ```
   Error: Could not save file
   ➡️ Solution: Create uploads/documents/ directory
   ```

### 🔧 Quick Fixes:
```bash
# 1. Check MongoDB
sudo systemctl status mongod

# 2. Create upload directory
mkdir -p uploads/documents/
mkdir -p uploads/thumbnails/

# 3. Check disk space
df -h

# 4. Check service logs
tail -f logs/documentservice.log
```

## 📞 Support & Contact

### 📧 API Documentation:
- Swagger UI: http://localhost:8084/swagger-ui.html
- API Docs: http://localhost:8084/v3/api-docs

### 🆘 Need Help?
- Check logs in `logs/documentservice.log`
- Use debug endpoints in `/api/debug/*`
- Test với Postman collection
- MongoDB connection issues: Verify `application.properties`
| **📊 Bảng tính** | XLS, XLSX | Bài tập Excel | 25MB |
| **🎭 Trình bày** | PPT, PPTX | Slide bài giảng | 100MB |
| **🖼️ Hình ảnh** | JPG, PNG, GIF | Hình minh họa | 10MB |

## 🎯 Auto-Detection Features

Khi upload file, hệ thống tự động:

1. **📝 Generate title** từ tên file (nếu không nhập)
2. **📂 Detect category** dựa trên extension:
   - `.pdf` → Giáo trình
   - `.docx` → Bài tập  
   - `.mp4` → Video
   - `.mp3` → Audio
   - `.pptx` → Hướng dẫn
3. **🏷️ Auto-tagging** dựa trên category và subject
4. **👤 Anonymous support** - không cần đăng nhập

## 📊 Upload hàng loạt (Bulk Upload)

Upload nhiều file cùng lúc:

```bash
curl -X POST "http://localhost:8084/api/documents/bulk-upload" \
  -F "files=@file1.pdf" \
  -F "files=@file2.docx" \
  -F "files=@video1.mp4" \
  -F "category=Giáo trình" \
  -F "gradeLevel=11" \
  -F "subject=Vật lý" \
  -F "uploadedBy=teacher_physics"
```

**Response:**
```json
{
  "success": true,
  "message": "Upload hoàn thành: 3 thành công, 0 lỗi",
  "data": [
    {
      "id": "doc123",
      "title": "file1",
      "category": "Giáo trình",
      "fileSize": 1048576
    }
  ]
}
```

## 🔍 Test Upload với curl

### Test upload file PDF:
```bash
curl -X POST "http://localhost:8084/api/documents/upload-simple" \
  -F "file=@test.pdf" \
  -F "title=Sách giáo khoa Toán 12" \
  -F "category=Giáo trình" \
  -F "gradeLevel=12" \
  -F "subject=Toán học"
```

### Test upload video:
```bash
curl -X POST "http://localhost:8084/api/documents/upload-simple" \
  -F "file=@bai_giang.mp4" \
  -F "title=Bài giảng Đạo hàm" \
  -F "category=Video" \
  -F "gradeLevel=11" \
  -F "subject=Toán học"
```

## ✅ Validation Rules

1. **File size limits:**
   - Video: Tối đa 500MB
   - Audio: Tối đa 100MB
   - Documents: Tối đa 50MB
   - Images: Tối đa 10MB

2. **Required fields:**
   - `file`: Bắt buộc
   - `title`: Bắt buộc (auto-generate nếu empty)

3. **Default values:**
   - `gradeLevel`: 12
   - `subject`: "Chung"
   - `category`: "Tài liệu"
   - `isPublic`: true
   - `uploadedBy`: "guest_user"

## 🎨 Response Format

### Success Response:
```json
{
  "success": true,
  "message": "Upload tài liệu thành công",
  "data": {
    "id": "67309b2c8e5d2f4a8c1234567",
    "title": "Giáo trình Toán 12",
    "description": "Sách giáo khoa Toán học lớp 12",
    "fileName": "toan_12_original.pdf",
    "fileType": "PDF",
    "fileUrl": "uploads/documents/uuid_toan_12.pdf",
    "fileSize": 2097152,
    "category": "Giáo trình",
    "gradeLevel": 12,
    "subject": "Toán học",
    "uploadedBy": "teacher_001",
    "downloadCount": 0,
    "viewCount": 0,
    "tags": "Giáo trình,Toán học,upload",
    "isPublic": true,
    "isActive": true,
    "createdAt": "2024-11-10T10:30:00",
    "updatedAt": "2024-11-10T10:30:00"
  }
}
```

### Error Response:
```json
{
  "success": false,
  "errorCode": 400,
  "message": "File không được để trống"
}
```

## 🔗 Sau khi upload thành công

File được upload sẽ có các URLs:

1. **📄 Xem chi tiết**: `/api/documents/{id}`
2. **👁️ Xem trực tuyến**: `/api/documents/view/{id}`
3. **📥 Tải xuống**: `/api/documents/download/{id}`
4. **🎬 Stream media**: `/api/documents/stream/{id}` (cho video/audio)
5. **🖼️ Thumbnail**: `/api/documents/thumbnail/{id}`

## 🛠️ Integration Examples

### JavaScript/jQuery:
```javascript
const formData = new FormData();
formData.append('file', fileInput.files[0]);
formData.append('title', 'Tài liệu mẫu');
formData.append('category', 'Giáo trình');

fetch('/api/documents/upload-simple', {
    method: 'POST',
    body: formData
})
.then(response => response.json())
.then(data => console.log('Upload success:', data));
```

### Python requests:
```python
import requests

files = {'file': open('document.pdf', 'rb')}
data = {
    'title': 'Tài liệu Python',
    'category': 'Hướng dẫn',
    'subject': 'Tin học'
}

response = requests.post(
    'http://localhost:8084/api/documents/upload-simple',
    files=files,
    data=data
)
print(response.json())
```

## 🚨 Troubleshooting

### Lỗi thường gặp:

1. **File quá lớn**: Kiểm tra giới hạn file size
2. **Định dạng không hỗ trợ**: Xem danh sách định dạng được chấp nhận
3. **Thiếu thư mục**: Service tự động tạo `uploads/documents/`
4. **Permission denied**: Kiểm tra quyền ghi file trong thư mục uploads

### Debug commands:

```bash
# Kiểm tra service health
curl http://localhost:8084/api/documents/health

# Xem danh sách tài liệu
curl http://localhost:8084/api/documents

# Kiểm tra thư mục upload
ls -la uploads/documents/
```

---

**🎓 Document Service v2.0** - Hệ thống quản lý tài liệu giáo dục hiện đại!
