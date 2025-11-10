package com.khoavdse170395.documentservice.controller;

import com.khoavdse170395.documentservice.dto.ApiResponse;
import com.khoavdse170395.documentservice.dto.DocumentRequestDto;
import com.khoavdse170395.documentservice.dto.DocumentResponseDto;
import com.khoavdse170395.documentservice.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.servlet.http.HttpServletRequest;

import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Document Management", description = "API quản lý tài liệu")
public class DocumentController {

    private final DocumentService documentService;

    @GetMapping("/health")
    @Operation(summary = "Health check endpoint")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Document Service is running!", "OK"));
    }

    @GetMapping
    @Operation(summary = "Lấy danh sách tất cả tài liệu")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getAllDocuments() {
        try {
            List<DocumentResponseDto> documents = documentService.getAllDocuments();
            return ResponseEntity.ok(ApiResponse.success("Lấy danh sách tài liệu thành công", documents));
        } catch (Exception e) {
            log.error("Error getting all documents: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Lấy chi tiết tài liệu theo ID")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> getDocumentById(@PathVariable String id) {
        try {
            Optional<DocumentResponseDto> document = documentService.getDocumentById(id);
            if (document.isPresent()) {
                // Increment view count
                documentService.incrementViewCount(id);
                return ResponseEntity.ok(ApiResponse.success("Lấy thông tin tài liệu thành công", document.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Không tìm thấy tài liệu"));
        } catch (Exception e) {
            log.error("Error getting document by ID: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload tài liệu mới")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> uploadDocument(
            @RequestPart("file") MultipartFile file,
            @RequestPart("document") DocumentRequestDto documentRequest,
            HttpServletRequest request) {
        try {
            // Lấy userId từ JWT hoặc dùng "anonymous" nếu không có
            String uploadedBy = (String) request.getAttribute("userId");
            if (uploadedBy == null || uploadedBy.isEmpty()) {
                uploadedBy = "anonymous_user";
            }

            DocumentResponseDto document = documentService.uploadDocument(file, documentRequest, uploadedBy);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Upload tài liệu thành công", document));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading document: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi upload tài liệu: " + e.getMessage()));
        }
    }

    @PostMapping("/upload-simple")
    @Operation(summary = "Upload tài liệu đơn giản - chỉ cần file và thông tin cơ bản")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> uploadDocumentSimple(
            @RequestParam("file") MultipartFile file,
            @RequestParam("title") String title,
            @RequestParam(value = "description", required = false, defaultValue = "") String description,
            @RequestParam(value = "category", required = false, defaultValue = "Tài liệu") String category,
            @RequestParam(value = "gradeLevel", required = false, defaultValue = "12") Integer gradeLevel,
            @RequestParam(value = "subject", required = false, defaultValue = "Chung") String subject,
            @RequestParam(value = "isPublic", required = false, defaultValue = "true") Boolean isPublic,
            @RequestParam(value = "uploadedBy", required = false, defaultValue = "guest_user") String uploadedBy) {
        try {
            // Tạo DocumentRequestDto từ các tham số
            DocumentRequestDto documentRequest = new DocumentRequestDto();
            documentRequest.setTitle(title);
            documentRequest.setDescription(description.isEmpty() ? "Tài liệu được upload từ " + uploadedBy : description);
            documentRequest.setCategory(category);
            documentRequest.setGradeLevel(gradeLevel);
            documentRequest.setSubject(subject);
            documentRequest.setIsPublic(isPublic);
            documentRequest.setTags(category + "," + subject + ",upload");

            DocumentResponseDto document = documentService.uploadDocument(file, documentRequest, uploadedBy);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success("Upload tài liệu thành công", document));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(ApiResponse.error(400, e.getMessage()));
        } catch (Exception e) {
            log.error("Error uploading document simple: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi upload tài liệu: " + e.getMessage()));
        }
    }

    @PostMapping("/bulk-upload")
    @Operation(summary = "Upload nhiều tài liệu cùng lúc")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> uploadMultipleDocuments(
            @RequestParam("files") MultipartFile[] files,
            @RequestParam(value = "category", required = false, defaultValue = "Tài liệu") String category,
            @RequestParam(value = "gradeLevel", required = false, defaultValue = "12") Integer gradeLevel,
            @RequestParam(value = "subject", required = false, defaultValue = "Chung") String subject,
            @RequestParam(value = "isPublic", required = false, defaultValue = "true") Boolean isPublic,
            @RequestParam(value = "uploadedBy", required = false, defaultValue = "bulk_uploader") String uploadedBy,
            HttpServletRequest request) {
        try {
            // Override uploadedBy nếu có trong JWT
            String actualUploader = (String) request.getAttribute("userId");
            if (actualUploader != null && !actualUploader.isEmpty()) {
                uploadedBy = actualUploader;
            }

            List<DocumentResponseDto> uploadedDocuments = new ArrayList<>();
            List<String> errors = new ArrayList<>();

            for (MultipartFile file : files) {
                try {
                    if (file.isEmpty()) {
                        errors.add("File " + file.getOriginalFilename() + " rỗng");
                        continue;
                    }

                    // Tạo DocumentRequestDto cho từng file
                    DocumentRequestDto documentRequest = new DocumentRequestDto();
                    documentRequest.setTitle(getFileNameWithoutExtension(file.getOriginalFilename()));
                    documentRequest.setDescription("Tài liệu upload hàng loạt: " + file.getOriginalFilename());
                    documentRequest.setCategory(category);
                    documentRequest.setGradeLevel(gradeLevel);
                    documentRequest.setSubject(subject);
                    documentRequest.setIsPublic(isPublic);
                    documentRequest.setTags(category + "," + subject + ",bulk-upload");

                    DocumentResponseDto document = documentService.uploadDocument(file, documentRequest, uploadedBy);
                    uploadedDocuments.add(document);

                } catch (Exception e) {
                    errors.add("Lỗi upload " + file.getOriginalFilename() + ": " + e.getMessage());
                }
            }

            String message = String.format("Upload hoàn thành: %d thành công, %d lỗi",
                    uploadedDocuments.size(), errors.size());

            if (!errors.isEmpty()) {
                log.warn("Bulk upload errors: {}", errors);
            }

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(ApiResponse.success(message, uploadedDocuments));

        } catch (Exception e) {
            log.error("Error in bulk upload: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi upload hàng loạt: " + e.getMessage()));
        }
    }

    @GetMapping("/upload-form")
    @Operation(summary = "Lấy form HTML để upload tài liệu")
    public ResponseEntity<String> getUploadForm() {
        try {
            String uploadFormHtml = generateUploadFormHtml();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                    .body(uploadFormHtml);
        } catch (Exception e) {
            log.error("Error generating upload form: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi tạo form upload");
        }
    }

    @PutMapping("/{id}")
    @Operation(summary = "Cập nhật thông tin tài liệu")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> updateDocument(
            @PathVariable String id,
            @RequestBody DocumentRequestDto documentRequest) {
        try {
            DocumentResponseDto document = documentService.updateDocument(id, documentRequest);
            return ResponseEntity.ok(ApiResponse.success("Cập nhật tài liệu thành công", document));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, e.getMessage()));
        } catch (Exception e) {
            log.error("Error updating document: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi cập nhật tài liệu: " + e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Xóa tài liệu")
    public ResponseEntity<ApiResponse<Void>> deleteDocument(@PathVariable String id) {
        try {
            boolean deleted = documentService.deleteDocument(id);
            if (deleted) {
                return ResponseEntity.ok(ApiResponse.success("Xóa tài liệu thành công", null));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Không tìm thấy tài liệu để xóa"));
        } catch (Exception e) {
            log.error("Error deleting document: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi xóa tài liệu: " + e.getMessage()));
        }
    }

    @GetMapping("/category/{category}")
    @Operation(summary = "Lấy tài liệu theo danh mục")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getDocumentsByCategory(@PathVariable String category) {
        try {
            List<DocumentResponseDto> documents = documentService.getDocumentsByCategory(category);
            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu theo danh mục thành công", documents));
        } catch (Exception e) {
            log.error("Error getting documents by category: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/grade/{gradeLevel}")
    @Operation(summary = "Lấy tài liệu theo lớp học")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getDocumentsByGradeLevel(@PathVariable Integer gradeLevel) {
        try {
            List<DocumentResponseDto> documents = documentService.getDocumentsByGradeLevel(gradeLevel);
            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu theo lớp học thành công", documents));
        } catch (Exception e) {
            log.error("Error getting documents by grade level: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/type/{fileType}")
    @Operation(summary = "Lấy tài liệu theo loại file")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getDocumentsByFileType(@PathVariable String fileType) {
        try {
            List<DocumentResponseDto> documents = documentService.getDocumentsByFileType(fileType);
            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu theo loại file thành công", documents));
        } catch (Exception e) {
            log.error("Error getting documents by file type: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/subject/{subject}")
    @Operation(summary = "Lấy tài liệu theo môn học")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getDocumentsBySubject(@PathVariable String subject) {
        try {
            List<DocumentResponseDto> documents = documentService.getDocumentsBySubject(subject);
            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu theo môn học thành công", documents));
        } catch (Exception e) {
            log.error("Error getting documents by subject: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Lấy tài liệu theo người dùng")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getDocumentsByUser(@PathVariable String userId) {
        try {
            List<DocumentResponseDto> documents = documentService.getDocumentsByUser(userId);
            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu theo người dùng thành công", documents));
        } catch (Exception e) {
            log.error("Error getting documents by user: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/public")
    @Operation(summary = "Lấy tài liệu công khai")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getPublicDocuments() {
        try {
            List<DocumentResponseDto> documents = documentService.getPublicDocuments();
            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu công khai thành công", documents));
        } catch (Exception e) {
            log.error("Error getting public documents: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/search")
    @Operation(summary = "Tìm kiếm tài liệu")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> searchDocuments(@RequestParam String keyword) {
        try {
            List<DocumentResponseDto> documents = documentService.searchDocuments(keyword);
            return ResponseEntity.ok(ApiResponse.success("Tìm kiếm tài liệu thành công", documents));
        } catch (Exception e) {
            log.error("Error searching documents: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/filter")
    @Operation(summary = "Lọc tài liệu nâng cao")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> filterDocuments(
            @RequestParam(required = false) String category,
            @RequestParam(required = false) Integer gradeLevel,
            @RequestParam(required = false) String fileType,
            @RequestParam(required = false) String subject) {
        try {
            List<DocumentResponseDto> documents;

            if (category != null && gradeLevel != null && fileType != null) {
                documents = documentService.getDocumentsByCategoryAndGradeLevelAndFileType(category, gradeLevel, fileType);
            } else if (category != null && gradeLevel != null) {
                documents = documentService.getDocumentsByCategoryAndGradeLevel(category, gradeLevel);
            } else if (category != null && fileType != null) {
                documents = documentService.getDocumentsByCategoryAndFileType(category, fileType);
            } else if (gradeLevel != null && subject != null) {
                documents = documentService.getDocumentsByGradeLevelAndSubject(gradeLevel, subject);
            } else {
                documents = documentService.getAllDocuments();
            }

            return ResponseEntity.ok(ApiResponse.success("Lọc tài liệu thành công", documents));
        } catch (Exception e) {
            log.error("Error filtering documents: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/popular/downloads")
    @Operation(summary = "Lấy tài liệu được tải nhiều nhất")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getMostDownloadedDocuments() {
        try {
            List<DocumentResponseDto> documents = documentService.getMostDownloadedDocuments();
            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu phổ biến thành công", documents));
        } catch (Exception e) {
            log.error("Error getting most downloaded documents: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/popular/views")
    @Operation(summary = "Lấy tài liệu được xem nhiều nhất")
    public ResponseEntity<ApiResponse<List<DocumentResponseDto>>> getMostViewedDocuments() {
        try {
            List<DocumentResponseDto> documents = documentService.getMostViewedDocuments();
            return ResponseEntity.ok(ApiResponse.success("Lấy tài liệu được xem nhiều nhất thành công", documents));
        } catch (Exception e) {
            log.error("Error getting most viewed documents: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/download/{id}")
    @Operation(summary = "Tải xuống tài liệu")
    public ResponseEntity<Resource> downloadDocument(@PathVariable String id) {
        try {
            String fileUrl = documentService.getFileUrl(id);
            Path filePath = Paths.get(fileUrl);
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                // Increment download count
                documentService.incrementDownloadCount(id);

                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION,
                               "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }

            return ResponseEntity.notFound().build();
        } catch (MalformedURLException e) {
            log.error("Error downloading document: ", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error downloading document: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/view/{id}")
    @Operation(summary = "Xem tài liệu trực tuyến")
    public ResponseEntity<Resource> viewDocument(@PathVariable String id,
                                                 @RequestHeader(value = "Range", required = false) String range) {
        try {
            Optional<DocumentResponseDto> documentOpt = documentService.getDocumentById(id);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            DocumentResponseDto document = documentOpt.get();
            String fileUrl = documentService.getFileUrl(id);
            Path filePath = Paths.get(fileUrl);

            // Nếu file không tồn tại, tạo file demo
            if (!filePath.toFile().exists()) {
                return createDemoFileResponse(document, id);
            }

            Resource resource = new UrlResource(filePath.toUri());

            if (!resource.exists() || !resource.isReadable()) {
                return createDemoFileResponse(document, id);
            }

            // Increment view count
            documentService.incrementViewCount(id);

            String fileType = document.getFileType().toLowerCase();

            // Handle different file types
            if (fileType.equals("mp4") || fileType.equals("avi") || fileType.equals("mov")) {
                // Video streaming with range support
                return handleVideoStreaming(resource, range);
            } else if (fileType.equals("mp3") || fileType.equals("wav") || fileType.equals("m4a")) {
                // Audio streaming
                return handleAudioStreaming(resource, range);
            } else if (fileType.equals("pdf")) {
                // PDF inline view
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .header(HttpHeaders.CONTENT_TYPE, "application/pdf")
                        .body(resource);
            } else if (fileType.equals("docx") || fileType.equals("doc")) {
                // Word documents - convert to HTML for viewing
                return createWordViewerResponse(document);
            } else {
                // Other files - inline view
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            }

        } catch (Exception e) {
            log.error("Error viewing document: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/preview/{id}")
    @Operation(summary = "Preview tài liệu với HTML viewer")
    public ResponseEntity<String> previewDocument(@PathVariable String id) {
        try {
            Optional<DocumentResponseDto> documentOpt = documentService.getDocumentById(id);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            DocumentResponseDto document = documentOpt.get();
            documentService.incrementViewCount(id);

            String previewHtml = generateDocumentPreviewHtml(document, id);

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                    .body(previewHtml);

        } catch (Exception e) {
            log.error("Error creating document preview: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi tạo preview tài liệu");
        }
    }

    @GetMapping("/test-view")
    @Operation(summary = "Test trang để thử tất cả tính năng view")
    public ResponseEntity<String> getTestViewPage() {
        try {
            String testPageHtml = generateTestViewPageHtml();
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                    .body(testPageHtml);
        } catch (Exception e) {
            log.error("Error generating test view page: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi tạo trang test");
        }
    }

    // Helper methods
    private ResponseEntity<Resource> createDemoFileResponse(DocumentResponseDto document, String id) {
        try {
            String demoContent = createDemoContent(document);
            Path demoPath = createTempFile(demoContent, document.getFileType());
            Resource resource = new UrlResource(demoPath.toUri());

            String contentType = getContentType(document.getFileType().toLowerCase());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, contentType)
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"demo_" + document.getFileName() + "\"")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error creating demo file: ", e);
            // Return plain text fallback
            String textContent = "Demo content for: " + document.getTitle() + "\n\nDescription: " + document.getDescription();
            ByteArrayResource textResource = new ByteArrayResource(textContent.getBytes());
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/plain; charset=UTF-8")
                    .body(textResource);
        }
    }

    private ResponseEntity<Resource> createWordViewerResponse(DocumentResponseDto document) {
        try {
            String htmlViewer = generateWordViewerHtml(document);
            Path tempPath = createTempFile(htmlViewer, "html");
            Resource resource = new UrlResource(tempPath.toUri());

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "text/html; charset=UTF-8")
                    .body(resource);

        } catch (Exception e) {
            log.error("Error creating Word viewer: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String createDemoContent(DocumentResponseDto document) {
        String fileType = document.getFileType().toLowerCase();

        switch (fileType) {
            case "pdf":
                return "This is a demo PDF content for: " + document.getTitle();
            case "txt":
                return "Demo text content:\n\n" + document.getTitle() + "\n\n" + document.getDescription();
            default:
                return generateDocumentPreviewHtml(document, document.getId());
        }
    }

    private Path createTempFile(String content, String extension) throws Exception {
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "document-service");
        if (!tempDir.toFile().exists()) {
            tempDir.toFile().mkdirs();
        }

        Path tempFile = tempDir.resolve("temp_" + System.currentTimeMillis() + "." + extension);
        java.nio.file.Files.write(tempFile, content.getBytes());
        return tempFile;
    }

    // HTML Generation Methods - Fixed to avoid HTML tags in controller
    private String generateWordViewerHtml(DocumentResponseDto document) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html lang=\"vi\">")
            .append("<head>")
            .append("<meta charset=\"UTF-8\">")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            .append("<title>").append(document.getTitle()).append(" - Document Viewer</title>")
            .append("<style>")
            .append("body { font-family: 'Times New Roman', serif; max-width: 800px; margin: 0 auto; padding: 40px 20px; line-height: 1.6; }")
            .append(".header { border-bottom: 2px solid #333; margin-bottom: 30px; padding-bottom: 20px; }")
            .append(".title { font-size: 24px; font-weight: bold; margin-bottom: 10px; }")
            .append(".meta { color: #666; font-size: 14px; }")
            .append(".content { font-size: 16px; text-align: justify; }")
            .append(".notice { background: #f0f8ff; border-left: 4px solid #0066cc; padding: 15px; margin: 20px 0; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<div class=\"header\">")
            .append("<div class=\"title\">").append(document.getTitle()).append("</div>")
            .append("<div class=\"meta\">📁 ").append(document.getCategory())
            .append(" | 🎓 Lớp ").append(document.getGradeLevel())
            .append(" | 📚 ").append(document.getSubject())
            .append(" | 👁️ ").append(document.getViewCount()).append(" lượt xem</div>")
            .append("</div>")
            .append("<div class=\"notice\">")
            .append("<strong>📄 Document Viewer</strong><br>")
            .append("Đây là phiên bản xem trực tuyến của tài liệu. File gốc có thể chưa được upload hoặc đang được xử lý.")
            .append("</div>")
            .append("<div class=\"content\">")
            .append("<h2>Mô tả tài liệu:</h2>")
            .append("<p>").append(document.getDescription() != null ? document.getDescription() : "Không có mô tả").append("</p>")
            .append("<h2>Thông tin chi tiết:</h2>")
            .append("<ul>")
            .append("<li><strong>Loại file:</strong> ").append(document.getFileType()).append("</li>")
            .append("<li><strong>Kích thước:</strong> ").append(formatFileSize(document.getFileSize())).append("</li>")
            .append("<li><strong>Người upload:</strong> ").append(document.getUploadedBy()).append("</li>")
            .append("<li><strong>Ngày tạo:</strong> ").append(document.getCreatedAt() != null ? document.getCreatedAt().toString() : "N/A").append("</li>")
            .append("</ul>")
            .append("<h2>Nội dung mẫu:</h2>")
            .append("<p>Đây là nội dung demo cho tài liệu <em>\"").append(document.getTitle()).append("\"</em>. Nội dung thực tế sẽ được hiển thị khi file gốc có sẵn.</p>")
            .append("<div style=\"margin-top: 30px; text-align: center;\">")
            .append("<a href=\"/api/documents/download/").append(document.getId()).append("\" style=\"background: #007bff; color: white; padding: 10px 20px; text-decoration: none; border-radius: 5px;\">")
            .append("📥 Tải xuống file gốc")
            .append("</a>")
            .append("</div>")
            .append("</div>")
            .append("</body>")
            .append("</html>");

        return html.toString();
    }

    private String generateDocumentPreviewHtml(DocumentResponseDto document, String id) {
        String fileType = document.getFileType().toLowerCase();

        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html lang=\"vi\">")
            .append("<head>")
            .append("<meta charset=\"UTF-8\">")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            .append("<title>").append(document.getTitle()).append(" - Preview</title>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; max-width: 1000px; margin: 0 auto; padding: 20px; background: #f5f5f5; }")
            .append(".container { background: white; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); overflow: hidden; }")
            .append(".header { background: linear-gradient(135deg, #667eea 0%, #764ba2 100%); color: white; padding: 30px; }")
            .append(".title { font-size: 28px; margin-bottom: 10px; }")
            .append(".meta { opacity: 0.9; font-size: 14px; }")
            .append(".content { padding: 30px; }")
            .append(".file-icon { font-size: 64px; text-align: center; margin: 20px 0; }")
            .append(".actions { display: flex; gap: 15px; justify-content: center; margin: 30px 0; }")
            .append(".btn { padding: 12px 24px; border: none; border-radius: 6px; text-decoration: none; color: white; font-weight: bold; transition: transform 0.2s; }")
            .append(".btn:hover { transform: translateY(-2px); }")
            .append(".btn-primary { background: #007bff; }")
            .append(".btn-success { background: #28a745; }")
            .append(".btn-info { background: #17a2b8; }")
            .append(".btn-danger { background: #dc3545; }")
            .append(".info-grid { display: grid; grid-template-columns: repeat(auto-fit, minmax(250px, 1fr)); gap: 20px; margin: 30px 0; }")
            .append(".info-card { background: #f8f9fa; padding: 20px; border-radius: 8px; border-left: 4px solid #007bff; }")
            .append(".notice { background: #fff3cd; border: 1px solid #ffeaa7; color: #856404; padding: 15px; border-radius: 5px; margin: 20px 0; }")
            .append(".media-container { text-align: center; margin: 20px 0; }")
            .append("video, audio { max-width: 100%; border-radius: 8px; }")
            .append("iframe { width: 100%; height: 600px; border: none; border-radius: 8px; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<div class=\"container\">")
            .append("<div class=\"header\">")
            .append("<div class=\"title\">📚 ").append(document.getTitle()).append("</div>")
            .append("<div class=\"meta\">")
            .append("📁 ").append(document.getCategory())
            .append(" • 🎓 Lớp ").append(document.getGradeLevel())
            .append(" • 📚 ").append(document.getSubject())
            .append(" • 👁️ ").append(document.getViewCount()).append(" lượt xem")
            .append(" • 📥 ").append(document.getDownloadCount()).append(" lượt tải")
            .append("</div>")
            .append("</div>")
            .append("<div class=\"content\">")
            .append(getPreviewMediaContent(document, id))
            .append("<div class=\"file-icon\">").append(getFileIcon(fileType)).append("</div>")
            .append("<div class=\"notice\">")
            .append("<strong>💡 Lưu ý:</strong> ").append(getFileNotice(fileType))
            .append("</div>")
            .append(getAdditionalContent(document, id))
            .append("<div class=\"info-grid\">")
            .append("<div class=\"info-card\">")
            .append("<h4>📋 Mô tả</h4>")
            .append("<p>").append(document.getDescription() != null ? document.getDescription() : "Chưa có mô tả").append("</p>")
            .append("</div>")
            .append("<div class=\"info-card\">")
            .append("<h4>📊 Thống kê</h4>")
            .append("<p>Loại: <strong>").append(document.getFileType()).append("</strong><br>")
            .append("Kích thước: <strong>").append(formatFileSize(document.getFileSize())).append("</strong><br>")
            .append("Upload bởi: <strong>").append(document.getUploadedBy()).append("</strong></p>")
            .append("</div>")
            .append("<div class=\"info-card\">")
            .append("<h4>🏷️ Tags</h4>")
            .append("<p>").append(document.getTags() != null ? document.getTags() : "Chưa có tags").append("</p>")
            .append("</div>")
            .append("<div class=\"info-card\">")
            .append("<h4>⏰ Thời gian</h4>")
            .append("<p>Tạo: ").append(document.getCreatedAt() != null ? document.getCreatedAt().toString() : "N/A").append("<br>")
            .append("Cập nhật: ").append(document.getUpdatedAt() != null ? document.getUpdatedAt().toString() : "N/A").append("</p>")
            .append("</div>")
            .append("</div>")
            .append("<div class=\"actions\">")
            .append("<a href=\"/api/documents/view/").append(id).append("\" class=\"btn btn-primary\">👁️ Xem trực tuyến</a>")
            .append("<a href=\"/api/documents/download/").append(id).append("\" class=\"btn btn-success\">📥 Tải xuống</a>")
            .append(getStreamingButton(fileType, id))
            .append("</div>")
            .append("</div>")
            .append("</div>")
            .append("</body>")
            .append("</html>");

        return html.toString();
    }

    private String generateUploadFormHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html lang=\"vi\">")
            .append("<head>")
            .append("<meta charset=\"UTF-8\">")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            .append("<title>Upload Tài liệu</title>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; max-width: 800px; margin: 50px auto; padding: 20px; background: #f5f5f5; }")
            .append(".container { background: white; padding: 30px; border-radius: 10px; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }")
            .append(".form-group { margin: 20px 0; }")
            .append("label { display: block; margin-bottom: 5px; font-weight: bold; color: #333; }")
            .append("input, select, textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; font-size: 16px; }")
            .append("textarea { height: 100px; resize: vertical; }")
            .append(".btn { background: #007bff; color: white; padding: 12px 24px; border: none; border-radius: 5px; cursor: pointer; font-size: 16px; }")
            .append(".btn:hover { background: #0056b3; }")
            .append(".btn:disabled { background: #ccc; cursor: not-allowed; }")
            .append("#result { margin-top: 20px; padding: 15px; border-radius: 5px; display: none; }")
            .append(".success { background: #d4edda; border: 1px solid #c3e6cb; color: #155724; }")
            .append(".error { background: #f8d7da; border: 1px solid #f5c6cb; color: #721c24; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<div class=\"container\">")
            .append("<h1>📚 Upload Tài liệu Học tập</h1>")
            .append("<p>Chọn file và điền thông tin để upload tài liệu vào hệ thống</p>")
            .append("<form id=\"uploadForm\">")
            .append("<div class=\"form-group\">")
            .append("<label for=\"file\">📎 Chọn file:</label>")
            .append("<input type=\"file\" id=\"file\" name=\"file\" required>")
            .append("<small style=\"color: #666;\">Hỗ trợ: PDF, Word, PowerPoint, Excel, Video, Audio</small>")
            .append("</div>")
            .append("<div class=\"form-group\">")
            .append("<label for=\"title\">📝 Tiêu đề:</label>")
            .append("<input type=\"text\" id=\"title\" name=\"title\" placeholder=\"Nhập tiêu đề tài liệu...\" required>")
            .append("</div>")
            .append("<div class=\"form-group\">")
            .append("<label for=\"description\">📋 Mô tả:</label>")
            .append("<textarea id=\"description\" name=\"description\" placeholder=\"Mô tả ngắn về nội dung tài liệu...\"></textarea>")
            .append("</div>")
            .append("<button type=\"submit\" class=\"btn\" id=\"uploadBtn\">")
            .append("🚀 Upload Tài liệu")
            .append("</button>")
            .append("</form>")
            .append("<div id=\"result\"></div>")
            .append("</div>")
            .append("</body>")
            .append("</html>");

        return html.toString();
    }

    private String generateTestViewPageHtml() {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>")
            .append("<html lang=\"vi\">")
            .append("<head>")
            .append("<meta charset=\"UTF-8\">")
            .append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">")
            .append("<title>🧪 Test Document Viewer</title>")
            .append("<style>")
            .append("body { font-family: Arial, sans-serif; max-width: 1200px; margin: 0 auto; padding: 20px; background: #f0f2f5; }")
            .append(".header { text-align: center; margin-bottom: 40px; }")
            .append(".section { background: white; margin: 30px 0; padding: 25px; border-radius: 12px; box-shadow: 0 2px 8px rgba(0,0,0,0.1); }")
            .append(".btn { background: #007bff; color: white; padding: 12px 20px; margin: 5px; border: none; border-radius: 6px; text-decoration: none; display: inline-block; font-weight: bold; transition: all 0.3s; }")
            .append(".btn:hover { background: #0056b3; transform: translateY(-1px); }")
            .append(".btn-success { background: #28a745; }")
            .append("</style>")
            .append("</head>")
            .append("<body>")
            .append("<div class=\"header\">")
            .append("<h1>🧪 Document Service - View Test Page</h1>")
            .append("<p>Test tất cả các tính năng xem tài liệu trực tuyến</p>")
            .append("</div>")
            .append("<div class=\"section\">")
            .append("<h2>🎯 Quick Test Actions</h2>")
            .append("<div style=\"text-align: center; margin: 30px 0;\">")
            .append("<a href=\"/api/documents\" class=\"btn\">📚 Test Get All Documents</a>")
            .append("<a href=\"/api/documents/upload-form\" class=\"btn btn-success\">📤 Open Upload Form</a>")
            .append("</div>")
            .append("</div>")
            .append("</body>")
            .append("</html>");

        return html.toString();
    }

    // Helper methods
    private String getFileIcon(String fileType) {
        switch (fileType) {
            case "pdf": return "📄";
            case "doc":
            case "docx": return "📝";
            case "mp4":
            case "avi":
            case "mov": return "🎬";
            case "mp3":
            case "wav": return "🔊";
            case "ppt":
            case "pptx": return "📊";
            case "xls":
            case "xlsx": return "📈";
            default: return "📄";
        }
    }

    private String getFileNotice(String fileType) {
        switch (fileType) {
            case "mp4":
            case "avi":
            case "mov": return "Video có thể được xem trực tuyến với player tích hợp.";
            case "mp3":
            case "wav": return "Audio có thể được phát trực tuyến.";
            case "pdf": return "PDF có thể được xem trực tuyến trong browser.";
            case "doc":
            case "docx": return "Document sẽ được chuyển đổi sang HTML để xem trực tuyến.";
            default: return "File có thể được tải xuống để xem chi tiết.";
        }
    }

    private String getPreviewMediaContent(DocumentResponseDto document, String id) {
        String fileType = document.getFileType().toLowerCase();

        switch (fileType) {
            case "mp4":
            case "avi":
            case "mov":
                return "<div class=\"media-container\">" +
                       "<video controls style=\"max-width: 100%; height: auto;\">" +
                       "<source src=\"/api/documents/stream/" + id + "\" type=\"video/" + fileType + "\">" +
                       "Your browser does not support the video tag." +
                       "</video>" +
                       "</div>";

            case "mp3":
            case "wav":
                return "<div class=\"media-container\">" +
                       "<audio controls style=\"width: 100%; max-width: 600px;\">" +
                       "<source src=\"/api/documents/stream/" + id + "\" type=\"audio/" + fileType + "\">" +
                       "Your browser does not support the audio element." +
                       "</audio>" +
                       "</div>";

            case "pdf":
                return "<div class=\"media-container\">" +
                       "<iframe src=\"/api/documents/view/" + id + "\" width=\"100%\" height=\"600px\">" +
                       "PDF không thể hiển thị. <a href=\"/api/documents/view/" + id + "\">Click để mở</a>" +
                       "</iframe>" +
                       "</div>";

            default:
                return "";
        }
    }

    private String getAdditionalContent(DocumentResponseDto document, String id) {
        if (document.getDurationSeconds() != null) {
            return "<div class=\"info-card\">" +
                   "<h4>⏱️ Thời lượng</h4>" +
                   "<p><strong>" + formatDuration(document.getDurationSeconds()) + "</strong></p>" +
                   "</div>";
        }
        return "";
    }

    private String getStreamingButton(String fileType, String id) {
        if (fileType.equals("mp4") || fileType.equals("mp3") || fileType.equals("wav")) {
            return "<a href=\"/api/media/player/" + id + "\" class=\"btn btn-info\">🎬 Media Player</a>";
        }
        return "";
    }

    private String formatDuration(Integer seconds) {
        if (seconds == null) return "N/A";
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;

        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }

    private String formatFileSize(Long bytes) {
        if (bytes == null) return "N/A";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private ResponseEntity<Resource> handleVideoStreaming(Resource resource, String range) {
        try {
            long fileSize = resource.contentLength();

            if (range == null) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                        .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                        .body(resource);
            }

            // Parse range header
            String[] ranges = range.replace("bytes=", "").split("-");
            long start = Long.parseLong(ranges[0]);
            long end = ranges.length > 1 && !ranges[1].isEmpty()
                    ? Long.parseLong(ranges[1])
                    : fileSize - 1;

            long contentLength = end - start + 1;

            return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                    .header(HttpHeaders.CONTENT_TYPE, "video/mp4")
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .header(HttpHeaders.CONTENT_LENGTH, String.valueOf(contentLength))
                    .header("Content-Range", "bytes " + start + "-" + end + "/" + fileSize)
                    .body(resource);

        } catch (Exception e) {
            log.error("Error in video streaming: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private ResponseEntity<Resource> handleAudioStreaming(Resource resource, String range) {
        try {
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_TYPE, "audio/mpeg")
                    .header(HttpHeaders.ACCEPT_RANGES, "bytes")
                    .body(resource);
        } catch (Exception e) {
            log.error("Error in audio streaming: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private String getContentType(String fileType) {
        switch (fileType.toLowerCase()) {
            case "mp4": return "video/mp4";
            case "avi": return "video/avi";
            case "mov": return "video/mov";
            case "mp3": return "audio/mpeg";
            case "wav": return "audio/wav";
            case "m4a": return "audio/m4a";
            default: return "application/octet-stream";
        }
    }

    private String getFileNameWithoutExtension(String fileName) {
        if (fileName == null || !fileName.contains(".")) {
            return fileName;
        }
        return fileName.substring(0, fileName.lastIndexOf('.'));
    }
}
