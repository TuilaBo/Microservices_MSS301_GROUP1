# DocumentService - Hệ thống Quản lý Tài liệu Văn học Việt Nam

## 🌟 Tổng quan
DocumentService là một microservice chuyên biệt trong hệ thống giáo dục trực tuyến, tập trung vào việc quản lý và phân phối tài liệu **Văn học Việt Nam** từ các giai đoạn lịch sử khác nhau.

## 🎯 Mục tiêu
- Digitalize tài liệu văn học Việt Nam 
- Hỗ trợ học tập trực tuyến môn Ngữ văn
- Cung cấp nền tảng streaming media cho giáo dục
- Quản lý tri thức văn học có hệ thống

## 📚 Nội dung Văn học

### 📖 Lớp 9 - Văn học Dân gian và Cổ điển
**Nội dung chính:**
- Ca dao, tục ngữ, thành ngữ Việt Nam
- Thần thoại và truyền thuyết dân gian
- Nghệ thuật ngôn ngữ dân gian
- Giá trị văn hóa truyền thống

**Tài liệu:**
- 📄 Giáo trình: Phân tích ca dao, tục ngữ
- 📝 Bài tập: Viết văn về văn học dân gian
- 🎬 Video: Hướng dẫn phân tích ca dao
- 🔊 Audio: Ngâm ca dao dân gian

### 📖 Lớp 10 - Văn học Trung đại
**Nội dung chính:**
- Nguyễn Trãi và "Bình Ngô đại cáo"
- Nguyễn Bỉnh Khiêm - triết lý Nho giáo
- Chữ Nôm và văn học viết bằng chữ Nôm
- Văn học thời Lý - Trần - Lê

**Tài liệu:**
- 📄 Giáo trình: Phân tích "Bình Ngô đại cáo"
- 📝 Bài tập: Nghị luận về nhân vật lịch sử
- 🎬 Video: Cuộc đời và sự nghiệp Nguyễn Trãi
- 📋 Tài liệu: Chữ Nôm và văn học cổ

### 📖 Lớp 11 - Văn học Hiện thực thế kỷ XIX
**Nội dung chính:**
- Nguyễn Du và "Truyện Kiều" 
- Hồ Xuân Hương - thơ phản kháng
- Nguyễn Đình Chiểu và "Lục Vân Tiên"
- Phong trào văn học hiện thực

**Tài liệu:**
- 📄 Giáo trình: Phân tích toàn diện "Truyện Kiều"
- 📝 Bài tập: So sánh Nguyễn Du và Hồ Xuân Hương
- 🎬 Video: Chủ đề tình yêu và số phận trong "Truyện Kiều"
- 🔊 Audio: Ngâm thơ Hồ Xuân Hương

### 📖 Lớp 12 - Văn học Hiện đại
**Nội dung chính:**
- Phong trào Thơ mới (Xuân Diệu, Huy Cận, Tế Hanh)
- Hồ Chí Minh với văn học và báo chí
- Tố Hữu - thơ ca cách mạng
- Văn học miền Nam trước 1975

**Tài liệu:**
- 📄 Giáo trình: Phong trào Thơ mới 1930-1945
- 📝 Bài tập: Phân tích tác phẩm Thơ mới
- 🎬 Video: Tinh thần cách mạng trong thơ Tố Hữu
- 📋 Đề thi: THPT Quốc gia môn Ngữ văn

## 🛠️ Tính năng Kỹ thuật

### 1. 📂 Quản lý Tài liệu
- **Multi-format Support**: PDF, DOCX, MP4, MP3, JPG, PNG
- **Metadata Management**: Title, description, category, grade, subject, tags
- **File Storage**: Local filesystem với cấu trúc tổ chức
- **Thumbnail Generation**: Tự động tạo thumbnail cho media files

### 2. 🎬 Media Streaming
- **Video Streaming**: Support HTTP Range requests cho streaming hiệu quả
- **Audio Streaming**: Phát audio trực tuyến không cần download
- **Progressive Download**: Cho phép xem/nghe trong khi đang download
- **Media Player Integration**: Embedded player trong web interface

### 3. 🔍 Search & Filter
- **Full-text Search**: Tìm kiếm theo title, description, tags
- **Category Filter**: Giáo trình, Bài tập, Video, Audio, Đề kiểm tra
- **Grade Level Filter**: Lớp 9, 10, 11, 12
- **Subject Filter**: Ngữ văn (có thể mở rộng)
- **Advanced Filter**: Kết hợp nhiều tiêu chí

### 4. 📊 Analytics & Statistics
- **View Tracking**: Đếm số lượt xem cho mỗi tài liệu
- **Download Tracking**: Thống kê lượt tải xuống
- **Popular Content**: Xác định tài liệu được quan tâm nhất
- **Usage Reports**: Báo cáo sử dụng hệ thống

### 5. 👥 User Management
- **Access Control**: Public/Private document management
- **Upload Tracking**: Theo dõi người upload
- **Activity Logging**: Log các hoạt động của user

## 🔗 API Architecture

### 📋 Core Document APIs
```http
# Document CRUD
GET    /api/documents                    # List all documents
GET    /api/documents/{id}               # Get document details
POST   /api/documents                    # Upload with metadata
PUT    /api/documents/{id}               # Update document info
DELETE /api/documents/{id}               # Delete document

# Simple upload variants
POST   /api/documents/upload-simple      # Basic upload form
POST   /api/documents/bulk-upload        # Multiple files at once
```

### 👁️ View & Stream APIs
```http
# Content access
GET /api/documents/view/{id}             # Stream/view content
GET /api/documents/download/{id}         # Download file
GET /api/documents/preview/{id}          # HTML preview page
GET /api/documents/upload-form           # Upload form page
GET /api/documents/test-view             # Test viewer page
```

### 🔍 Search & Filter APIs
```http
# Filtering
GET /api/documents/category/{category}   # By category
GET /api/documents/grade/{gradeLevel}    # By grade level
GET /api/documents/type/{fileType}       # By file type
GET /api/documents/subject/{subject}     # By subject
GET /api/documents/user/{userId}         # By uploader
GET /api/documents/public                # Public documents only

# Search & advanced filter
GET /api/documents/search?keyword={term} # Full-text search
GET /api/documents/filter?category=...   # Advanced multi-filter
```

### 📊 Statistics APIs
```http
# Popular content
GET /api/documents/popular/downloads     # Most downloaded
GET /api/documents/popular/views         # Most viewed
```



## 🗄️ Data Model

### DocumentEntity Structure
```javascript
{
  "id": "string",                        // MongoDB ObjectId
  "title": "string",                     // Document title
  "description": "string",               // Detailed description
  "fileName": "string",                  // Original filename
  "fileType": "string",                  // PDF, DOCX, MP4, MP3, etc.
  "fileSize": "number",                  // File size in bytes
  "fileUrl": "string",                   // Storage path
  "thumbnailUrl": "string",              // Thumbnail image path
  "category": "string",                  // Giáo trình, Bài tập, Video, Audio, Đề kiểm tra
  "gradeLevel": "number",                // 9, 10, 11, 12
  "subject": "string",                   // Ngữ văn
  "tags": "string",                      // Comma-separated tags
  "isPublic": "boolean",                 // Public access flag
  "isActive": "boolean",                 // Active status
  "uploadedBy": "string",                // Uploader identifier
  "viewCount": "number",                 // View statistics
  "downloadCount": "number",             // Download statistics
  "durationSeconds": "number",           // For audio/video files
  "createdAt": "datetime",               // Creation timestamp
  "updatedAt": "datetime"                // Last update timestamp
}
```

## 🎨 Frontend Integration

### HTML Viewers
- **Document Preview**: Rich HTML preview cho các loại tài liệu
- **Video Player**: Embedded HTML5 video player với controls
- **Audio Player**: HTML5 audio player cho file âm thanh
- **Upload Form**: Form upload với drag & drop support

### Responsive Design
- **Mobile-friendly**: Responsive design cho mobile/tablet
- **Progressive Enhancement**: Graceful degradation cho older browsers
- **Accessibility**: ARIA support cho screen readers

## 🔒 Security Features

### Access Control
- **Public/Private Documents**: Fine-grained access control
- **JWT Integration**: Token-based authentication (validation only)
- **File Type Validation**: Whitelist allowed file extensions
- **Size Limits**: Max file size restrictions (100MB)

### Data Protection
- **Input Sanitization**: Clean user inputs để prevent XSS
- **Path Traversal Prevention**: Secure file path handling
- **CORS Configuration**: Cross-origin request handling

## 🚀 Performance Optimizations

### Caching Strategy
- **MongoDB Indexing**: Optimized queries cho search/filter
- **File Caching**: Browser caching headers cho static content
- **Thumbnail Caching**: Pre-generated thumbnails

### Streaming Efficiency
- **HTTP Range Support**: Partial content delivery
- **Chunked Transfer**: Efficient large file handling
- **Compression**: Gzip compression cho text-based files

## 📊 Monitoring & Logging

### Application Metrics
- **Document Statistics**: Total, by category, by grade
- **Usage Analytics**: Most popular content
- **Performance Metrics**: Response times, error rates

### Logging Strategy
- **Structured Logging**: JSON format với correlation IDs
- **Error Tracking**: Detailed error logging với stack traces
- **Audit Trail**: User action logging cho compliance

## 🔧 Configuration Management

### Environment-specific Settings
```properties
# MongoDB Configuration
spring.data.mongodb.host=localhost
spring.data.mongodb.port=27017
spring.data.mongodb.database=document

# File Storage
app.upload.dir=uploads/documents/
spring.servlet.multipart.max-file-size=100MB

# Security
jwt.secret=[your-secret-key]
jwt.expiration-ms=86400000
```

### Feature Flags
- **Debug Mode**: Enable/disable debug endpoints
- **Auto-initialization**: Automatic sample data creation
- **Media Streaming**: Enable/disable streaming features

## 🧪 Testing Strategy

### Unit Tests
- **Service Layer**: Business logic testing
- **Repository Layer**: Database interaction testing
- **Utility Methods**: Helper function testing

### Integration Tests
- **API Endpoints**: Full request/response cycle testing
- **Database Integration**: MongoDB connection testing
- **File Operations**: Upload/download testing

### Performance Tests
- **Load Testing**: High concurrent user simulation
- **Stress Testing**: System limits testing
- **Media Streaming**: Large file handling testing

## 📈 Future Enhancements

### Planned Features
- **AI-powered Search**: Semantic search với NLP
- **Content Recommendation**: ML-based content suggestions
- **Collaborative Features**: Comments, ratings, reviews
- **Advanced Analytics**: Learning analytics dashboard

### Scalability Improvements
- **Microservice Decomposition**: Split theo domains
- **Distributed Storage**: Cloud storage integration
- **CDN Integration**: Global content delivery
- **Event-driven Architecture**: Async processing với message queues
GET /api/documents/type/{fileType}          - Lọc theo loại file
GET /api/documents/search?keyword={query}   - Tìm kiếm
```

### Media Management (`/api/media`)

```
GET /api/media/preview/{id}     - Xem trước (không tăng view count)
GET /api/media/embed/{id}       - HTML embed cho media
GET /api/media/player/{id}      - Media player đầy đủ tính năng
GET /api/media/info/{id}        - Thông tin chi tiết media
GET /api/media/subtitle/{id}    - Lấy subtitle cho video
```

### Educational Content (`/api/educational`)

```
GET /api/educational/lesson-materials/{gradeLevel}/{subject}
    - Tài liệu học tập theo lớp và môn
    
GET /api/educational/curriculum/{gradeLevel}
    - Chương trình học theo lớp
    
GET /api/educational/exercise-materials/{gradeLevel}/{subject}
    - Bài tập và đề kiểm tra
    
GET /api/educational/teaching-materials/{gradeLevel}/{subject}
    - Tài liệu giảng dạy
    
GET /api/educational/multimedia/{gradeLevel}/{subject}
    - Tài liệu đa phương tiện
    
GET /api/educational/study-plan/{gradeLevel}
    - Kế hoạch học tập đề xuất
    
GET /api/educational/popular-content/{gradeLevel}
    - Nội dung phổ biến theo lớp
```

## Các loại tài liệu được hỗ trợ

### Tài liệu văn bản:
- **PDF**: Xem inline, tải xuống
- **DOCX/DOC**: Tải xuống, thumbnail
- **TXT**: Xem trực tuyến

### Tài liệu đa phương tiện:
- **Video**: MP4, AVI, MOV, WEBM - Streaming với HTML5 player
- **Audio**: MP3, WAV, M4A, OGG - Streaming audio player
- **Hỗ trợ**: Subtitle (.srt, .vtt), thumbnail tự động

### Danh mục tài liệu:
- **Hướng dẫn**: Tài liệu hướng dẫn sử dụng
- **Giáo trình**: Tài liệu giảng dạy chính thức  
- **Bài tập**: Đề bài và bài tập thực hành
- **Video**: Video bài giảng
- **Audio**: Tài liệu âm thanh
- **Tham khảo**: Tài liệu bổ sung

## Tính năng nâng cao

### 1. Video/Audio Streaming
- **Range requests**: Hỗ trợ tua nhanh, tua chậm
- **Adaptive streaming**: Tự động điều chỉnh chất lượng
- **Media controls**: Play, pause, volume, fullscreen
- **Subtitle support**: Tự động detect file .srt cùng tên

### 2. Quản lý Nội dung Giáo dục
- **Phân loại thông minh**: Tự động group theo môn học, lớp
- **Kế hoạch học tập**: Đề xuất thứ tự học dựa trên curriculum
- **Thống kê sử dụng**: View count, download count
- **Gợi ý nội dung**: Dựa trên popularity và relevance

### 3. Thumbnail & Preview
- **Auto thumbnail**: Tự động tạo cho video, PDF
- **Default icons**: Icon mặc định cho mỗi loại file
- **Preview mode**: Xem thông tin không tăng view count

### 4. Security & Permissions
- **Public/Private**: Kiểm soát quyền truy cập
- **User-based**: Tài liệu theo người upload
- **JWT Authentication**: Bảo mật API

## Cấu hình

### Upload Directory
```
uploads/documents/     - Thư mục chứa file upload
static/thumbnails/     - Thư mục chứa thumbnail mặc định
```

### File Size Limits
- **Video**: Max 500MB
- **Audio**: Max 100MB  
- **Documents**: Max 50MB

## Ví dụ sử dụng

### 1. Upload video bài giảng:
```bash
POST /api/documents
Content-Type: multipart/form-data

{
  "file": video_file.mp4,
  "document": {
    "title": "Bài giảng Văn học Việt Nam",
    "description": "Video phân tích tác phẩm Truyện Kiều",
    "category": "Video",
    "gradeLevel": 12,
    "subject": "Ngữ văn",
    "isPublic": true
  }
}
```

### 2. Xem video trực tuyến:
```html
<!-- Sử dụng media player -->
<iframe src="/api/media/player/{id}" width="800" height="600"></iframe>

<!-- Hoặc stream trực tiếp -->
<video controls>
  <source src="/api/documents/stream/{id}" type="video/mp4">
</video>
```

### 3. Lấy tài liệu theo chương trình học:
```bash
GET /api/educational/curriculum/12
```

Response:
```json
{
  "success": true,
  "data": {
    "gradeLevel": 12,
    "totalDocuments": 25,
    "subjects": ["Ngữ văn", "Toán học", "Tiếng Anh"],
    "curriculum": {
      "Ngữ văn": {
        "Giáo trình": [...],
        "Video": [...],
        "Bài tập": [...]
      }
    }
  }
}
```

## Integration với các Microservices khác

### LessonService
- Liên kết tài liệu với bài học
- Tracking progress qua document views

### AccountService  
- Authentication cho upload/download
- User permissions

### PaymentService
- Premium content access
- Paid document downloads

### AIService
- Content recommendation
- Auto-tagging documents
- Quality assessment

## Performance & Scalability

### Caching
- Document metadata cache
- Thumbnail cache
- Popular content cache

### CDN Support
- Static file serving
- Video streaming optimization
- Global content distribution

### Database Optimization
- MongoDB indexing on category, subject, gradeLevel
- Full-text search on title, description, tags
- Aggregation pipelines for statistics

## Monitoring & Analytics

### Metrics
- Document upload/download rates
- Popular content tracking  
- User engagement analytics
- Storage utilization

### Logging
- File operations
- Streaming sessions
- Error tracking
- Performance monitoring

## Dữ liệu mẫu được khởi tạo

Khi khởi động lần đầu, hệ thống sẽ tự động tạo dữ liệu mẫu đa dạng bao gồm:

### 📚 Môn học và lớp:
- **Ngữ văn**: Lớp 9-12 (50+ tài liệu)
- **Toán học**: Lớp 9-12 (40+ tài liệu) 
- **Tiếng Anh**: Lớp 9-12 (30+ tài liệu)
- **Vật lý**: Lớp 10-12 (25+ tài liệu)
- **Hóa học**: Lớp 10-12 (20+ tài liệu)
- **Sinh học**: Lớp 9-12 (25+ tài liệu)
- **Lịch sử**: Lớp 10-12 (15+ tài liệu)
- **Địa lý**: Lớp 10-12 (15+ tài liệu)
- **GDCD**: Lớp 9-12 (10+ tài liệu)
- **Tin học**: Lớp 9-12 (15+ tài liệu)

### 📁 Loại tài liệu:
- **Giáo trình**: Tài liệu giảng dạy chính thức
- **Bài tập**: Đề bài và bài tập thực hành
- **Video**: Video bài giảng và demo
- **Audio**: File âm thanh học tập
- **Hướng dẫn**: Tài liệu hướng dẫn chi tiết
- **Đề kiểm tra**: Đề thi và kiểm tra
- **Tham khảo**: Tài liệu tham khảo bổ sung

### 🗂️ Định dạng file:
- **PDF**: Giáo trình, đề thi, tài liệu tham khảo
- **DOCX**: Bài tập, hướng dẫn
- **MP4**: Video bài giảng 
- **MP3**: Audio lessons
- **XLSX**: Bài tập Excel, bảng tính

### 📊 Thống kê dữ liệu mẫu:
- **Tổng số tài liệu**: 250+ documents
- **Lượt xem**: 10-500 views mỗi tài liệu
- **Lượt tải**: 1-250 downloads mỗi tài liệu
- **Thời gian tạo**: Ngẫu nhiên trong 6 tháng qua
- **Người upload**: Giáo viên và quản trị viên hệ thống

### 🎯 Tính năng đặc biệt của dữ liệu mẫu:
- **Realistic file sizes**: Kích thước file phù hợp với từng loại
- **Duration for media**: Video/audio có thời lượng thực tế
- **Thumbnail URLs**: Đường dẫn thumbnail cho media files
- **Rich metadata**: Tags, descriptions chi tiết cho từng tài liệu
- **Diverse subjects**: Đa dạng môn học từ cơ bản đến nâng cao

### 🔄 Auto-generated features:
- **Random statistics**: View count, download count ngẫu nhiên thực tế
- **Time-based data**: Created/updated timestamps trong khoảng thời gian hợp lý
- **Multiple uploaders**: Nhiều user khác nhau upload tài liệu
- **Varied file sizes**: Kích thước file phù hợp với loại tài liệu

Dữ liệu này giúp demo đầy đủ các tính năng của hệ thống mà không cần upload thủ công!
