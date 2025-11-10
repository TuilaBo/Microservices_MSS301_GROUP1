# DocumentService - Hệ thống Quản lý Tài liệu Văn học Việt Nam

## 📚 Mô tả
DocumentService là một microservice chuyên quản lý tài liệu học tập môn **Ngữ văn** với focus vào **Văn học Việt Nam**:

### 🎯 Tính năng chính:
- 📖 Quản lý tài liệu Văn học Việt Nam (các giai đoạn từ dân gian đến hiện đại)
- 🎬 Streaming video bài giảng và audio ngâm thơ
- 📝 Bài tập và đề kiểm tra môn Ngữ văn
- 🔍 Tìm kiếm và phân loại theo lớp, tác giả, tác phẩm
- 📊 Thống kê lượt xem, tải xuống
- 💾 Upload/Download đa định dạng (PDF, DOCX, MP4, MP3)

### 📖 Nội dung Văn học:
- **Lớp 9**: Văn học dân gian (ca dao, tục ngữ, thần thoại)
- **Lớp 10**: Văn học trung đại (Nguyễn Trãi, Nguyễn Bỉnh Khiêm)
- **Lớp 11**: Văn học hiện thực thế kỷ XIX (Nguyễn Du, Hồ Xuân Hương)
- **Lớp 12**: Văn học hiện đại (Thơ mới, Tố Hữu, Hồ Chí Minh)

## 🛠️ Cấu hình kỹ thuật
- **Port**: 8084
- **Database**: MongoDB (document)
- **File storage**: uploads/documents/
- **Max file size**: 100MB
- **Supported formats**: PDF, DOCX, MP4, MP3, JPG, PNG

## 🚀 API Endpoints

### 📋 Document Management
```http
GET    /api/documents                    # Danh sách tài liệu
GET    /api/documents/{id}               # Chi tiết tài liệu
POST   /api/documents/upload-simple      # Upload đơn giản
POST   /api/documents/bulk-upload        # Upload hàng loạt
PUT    /api/documents/{id}               # Cập nhật
DELETE /api/documents/{id}               # Xóa
```

### 👁️ View & Stream
```http
GET /api/documents/view/{id}             # Xem trực tuyến
GET /api/documents/download/{id}         # Tải xuống
GET /api/documents/preview/{id}          # Preview HTML
GET /api/documents/upload-form           # Form upload
```

### 🔍 Filter & Search
```http
GET /api/documents/category/{category}   # Lọc theo danh mục
GET /api/documents/grade/{gradeLevel}    # Lọc theo lớp
GET /api/documents/subject/{subject}     # Lọc môn Ngữ văn
GET /api/documents/search?keyword=       # Tìm kiếm
GET /api/documents/filter               # Lọc nâng cao
```

### 📊 Statistics
```http
GET /api/documents/popular/downloads     # Được tải nhiều nhất
GET /api/documents/popular/views         # Được xem nhiều nhất
```

### 🧪 Debug & Testing
```http
GET  /api/debug/count                   # Đếm số tài liệu
GET  /api/debug/all                     # Xem tất cả
POST /api/debug/test-save               # Test save
POST /api/debug/force-init              # Tạo data mẫu
DELETE /api/debug/clear                 # Xóa tất cả
```

## 📂 Danh mục tài liệu

### 📖 Loại tài liệu:
- **Giáo trình**: Lý thuyết văn học, phân tích tác phẩm
- **Bài tập**: Phân tích, viết văn, nghị luận
- **Video**: Bài giảng trực quan, phân tích tác phẩm
- **Audio**: Ngâm thơ, đọc tác phẩm
- **Đề kiểm tra**: Đề 15 phút, giữa kỳ, cuối kỳ, THPT QG

### 🎭 Tác giả & Tác phẩm:
- **Nguyễn Du**: Truyện Kiều
- **Hồ Xuân Hương**: Thơ phản kháng
- **Nguyễn Trãi**: Bình Ngô đại cáo
- **Tố Hữu**: Thơ cách mạng
- **Xuân Diệu**: Thơ mới

## 🚀 Khởi chạy

### Yêu cầu:
- Java 17+
- MongoDB running on localhost:27017
- Maven 3.6+

### Chạy ứng dụng:
```bash
# Clone và build
git clone [repository]
cd DocumentService
mvn clean install

# Chạy
mvn spring-boot:run
# hoặc
java -jar target/DocumentService-1.0.0.jar
```

### Khởi tạo data mẫu:
```bash
# Tự động khởi tạo khi start lần đầu
# Hoặc force init qua API:
POST http://localhost:8084/api/debug/force-init
```

## 🌐 Giao diện

### Swagger UI:
- URL: http://localhost:8084/swagger-ui.html
- API Docs: http://localhost:8084/v3/api-docs

### Health Check:
- URL: http://localhost:8084/api/documents/health
- Response: `{"status": 200, "message": "Document Service is running!", "data": "OK"}`

## 📡 Microservice Integration

### Service Discovery:
- **Eureka Server**: http://localhost:8761/eureka
- **Service Name**: DocumentService

### Port mapping:
- **DocumentService**: 8084
- **LessonService**: 8083
- **AccountService**: 8082
- **EurekaServer**: 8761

## 🐛 Troubleshooting

### MongoDB connection issues:
```bash
# Check MongoDB status
sudo systemctl status mongod

# Check connection
mongo --host localhost:27017
```

### Empty data response:
```bash
# Check document count
GET /api/debug/count

# Force reinitialize data
POST /api/debug/force-init
```

### File upload issues:
- Check `uploads/documents/` directory exists
- Verify file size < 100MB
- Supported formats: PDF, DOCX, MP4, MP3

## 📝 Logs
- Application logs: `logs/documentservice.log`
- Error logs: `logs/error.log`
- Debug level: `logging.level.com.khoavdse170395.documentservice=DEBUG`
