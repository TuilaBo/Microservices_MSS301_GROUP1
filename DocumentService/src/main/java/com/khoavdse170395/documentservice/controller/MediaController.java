package com.khoavdse170395.documentservice.controller;

import com.khoavdse170395.documentservice.dto.ApiResponse;
import com.khoavdse170395.documentservice.dto.DocumentResponseDto;
import com.khoavdse170395.documentservice.service.DocumentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

@RestController
@RequestMapping("/api/media")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Media Management", description = "API quản lý media và streaming")
public class MediaController {

    private final DocumentService documentService;

    @GetMapping("/preview/{id}")
    @Operation(summary = "Xem trước tài liệu (không tăng view count)")
    public ResponseEntity<ApiResponse<DocumentResponseDto>> previewDocument(@PathVariable String id) {
        try {
            Optional<DocumentResponseDto> document = documentService.getDocumentById(id);
            if (document.isPresent()) {
                return ResponseEntity.ok(ApiResponse.success("Lấy thông tin preview thành công", document.get()));
            }
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ApiResponse.error(404, "Không tìm thấy tài liệu"));
        } catch (Exception e) {
            log.error("Error getting document preview: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/embed/{id}")
    @Operation(summary = "Nhúng media để hiển thị trên web")
    public ResponseEntity<String> embedMedia(@PathVariable String id) {
        try {
            Optional<DocumentResponseDto> documentOpt = documentService.getDocumentById(id);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            DocumentResponseDto document = documentOpt.get();
            String fileType = document.getFileType().toLowerCase();
            String embedHtml = generateEmbedHtml(id, document, fileType);

            if (embedHtml != null) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "text/html")
                        .body(embedHtml);
            }

            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error generating embed HTML: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/player/{id}")
    @Operation(summary = "Media player cho video/audio")
    public ResponseEntity<String> mediaPlayer(@PathVariable String id) {
        try {
            Optional<DocumentResponseDto> documentOpt = documentService.getDocumentById(id);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            DocumentResponseDto document = documentOpt.get();
            String fileType = document.getFileType().toLowerCase();

            if (isMediaFile(fileType)) {
                String playerHtml = generatePlayerHtml(id, document, fileType);
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_TYPE, "text/html")
                        .body(playerHtml);
            }

            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("Error generating media player: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/info/{id}")
    @Operation(summary = "Thông tin chi tiết file media")
    public ResponseEntity<ApiResponse<Object>> getMediaInfo(@PathVariable String id) {
        try {
            Optional<DocumentResponseDto> documentOpt = documentService.getDocumentById(id);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(ApiResponse.error(404, "Không tìm thấy tài liệu"));
            }

            DocumentResponseDto document = documentOpt.get();
            
            // Tạo object thông tin media
            Object mediaInfo = createMediaInfo(document);
            
            return ResponseEntity.ok(ApiResponse.success("Lấy thông tin media thành công", mediaInfo));
        } catch (Exception e) {
            log.error("Error getting media info: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(ApiResponse.error(500, "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    @GetMapping("/subtitle/{id}")
    @Operation(summary = "Lấy subtitle cho video (nếu có)")
    public ResponseEntity<Resource> getSubtitle(@PathVariable String id) {
        try {
            Optional<DocumentResponseDto> documentOpt = documentService.getDocumentById(id);
            if (documentOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // Tìm file subtitle (.srt, .vtt) cùng tên với video
            String fileUrl = documentService.getFileUrl(id);
            Path videoPath = Paths.get(fileUrl);
            String baseName = getBaseName(videoPath.getFileName().toString());
            
            // Thử tìm các file subtitle
            String[] subtitleExts = {".srt", ".vtt", ".sub"};
            for (String ext : subtitleExts) {
                Path subtitlePath = videoPath.getParent().resolve(baseName + ext);
                if (Files.exists(subtitlePath)) {
                    Resource resource = new UrlResource(subtitlePath.toUri());
                    return ResponseEntity.ok()
                            .header(HttpHeaders.CONTENT_TYPE, "text/vtt")
                            .body(resource);
                }
            }

            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("Error getting subtitle: ", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    private boolean isMediaFile(String fileType) {
        return fileType.equals("mp4") || fileType.equals("avi") || fileType.equals("mov") ||
               fileType.equals("mp3") || fileType.equals("wav") || fileType.equals("m4a") ||
               fileType.equals("webm") || fileType.equals("ogg");
    }

    private String generateEmbedHtml(String id, DocumentResponseDto document, String fileType) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>").append(document.getTitle()).append("</title>");
        html.append("<style>");
        html.append("body { margin: 0; padding: 0; background: #000; }");
        html.append(".container { width: 100%; height: 100vh; display: flex; align-items: center; justify-content: center; }");
        html.append("video, audio { max-width: 100%; max-height: 100%; }");
        html.append("</style>");
        html.append("</head><body>");

        if (fileType.equals("mp4") || fileType.equals("webm") || fileType.equals("avi")) {
            html.append("<div class='container'>");
            html.append("<video controls autoplay>");
            html.append("<source src='/api/documents/stream/").append(id).append("' type='video/").append(fileType).append("'>");
            html.append("Your browser does not support the video tag.");
            html.append("</video>");
            html.append("</div>");
        } else if (fileType.equals("mp3") || fileType.equals("wav") || fileType.equals("ogg")) {
            html.append("<div class='container'>");
            html.append("<audio controls autoplay>");
            html.append("<source src='/api/documents/stream/").append(id).append("' type='audio/").append(fileType).append("'>");
            html.append("Your browser does not support the audio tag.");
            html.append("</audio>");
            html.append("</div>");
        } else {
            return null;
        }

        html.append("</body></html>");
        return html.toString();
    }

    private String generatePlayerHtml(String id, DocumentResponseDto document, String fileType) {
        StringBuilder html = new StringBuilder();
        html.append("<!DOCTYPE html>");
        html.append("<html><head>");
        html.append("<meta charset='UTF-8'>");
        html.append("<meta name='viewport' content='width=device-width, initial-scale=1.0'>");
        html.append("<title>").append(document.getTitle()).append("</title>");
        html.append("<style>");
        html.append("body { font-family: Arial, sans-serif; margin: 0; padding: 20px; background: #f5f5f5; }");
        html.append(".player-container { max-width: 800px; margin: 0 auto; background: white; border-radius: 8px; overflow: hidden; box-shadow: 0 2px 10px rgba(0,0,0,0.1); }");
        html.append(".player-header { padding: 20px; border-bottom: 1px solid #eee; }");
        html.append(".player-title { font-size: 24px; font-weight: bold; margin: 0 0 10px 0; }");
        html.append(".player-info { color: #666; font-size: 14px; }");
        html.append(".player-media { position: relative; background: #000; }");
        html.append("video, audio { width: 100%; height: auto; }");
        html.append(".player-controls { padding: 20px; }");
        html.append(".control-button { background: #007bff; color: white; border: none; padding: 8px 16px; margin: 5px; border-radius: 4px; cursor: pointer; }");
        html.append(".control-button:hover { background: #0056b3; }");
        html.append("</style>");
        html.append("</head><body>");

        html.append("<div class='player-container'>");
        html.append("<div class='player-header'>");
        html.append("<h1 class='player-title'>").append(document.getTitle()).append("</h1>");
        html.append("<div class='player-info'>");
        html.append("Loại file: ").append(document.getFileType().toUpperCase());
        if (document.getDurationSeconds() != null) {
            html.append(" | Thời lượng: ").append(formatDuration(document.getDurationSeconds()));
        }
        html.append(" | Lượt xem: ").append(document.getViewCount());
        html.append("</div>");
        html.append("</div>");

        html.append("<div class='player-media'>");
        if (fileType.equals("mp4") || fileType.equals("webm") || fileType.equals("avi")) {
            html.append("<video id='mediaPlayer' controls>");
            html.append("<source src='/api/documents/stream/").append(id).append("' type='video/").append(fileType).append("'>");
            html.append("Your browser does not support the video tag.");
            html.append("</video>");
        } else if (fileType.equals("mp3") || fileType.equals("wav") || fileType.equals("ogg")) {
            html.append("<audio id='mediaPlayer' controls>");
            html.append("<source src='/api/documents/stream/").append(id).append("' type='audio/").append(fileType).append("'>");
            html.append("Your browser does not support the audio tag.");
            html.append("</audio>");
        }
        html.append("</div>");

        html.append("<div class='player-controls'>");
        html.append("<button class='control-button' onclick='downloadFile()'>Tải xuống</button>");
        html.append("<button class='control-button' onclick='toggleFullscreen()'>Toàn màn hình</button>");
        html.append("</div>");

        html.append("</div>");

        // JavaScript
        html.append("<script>");
        html.append("function downloadFile() {");
        html.append("  window.location.href = '/api/documents/download/").append(id).append("';");
        html.append("}");
        html.append("function toggleFullscreen() {");
        html.append("  const player = document.getElementById('mediaPlayer');");
        html.append("  if (player.requestFullscreen) player.requestFullscreen();");
        html.append("  else if (player.webkitRequestFullscreen) player.webkitRequestFullscreen();");
        html.append("  else if (player.msRequestFullscreen) player.msRequestFullscreen();");
        html.append("}");
        html.append("</script>");

        html.append("</body></html>");
        return html.toString();
    }

    private Object createMediaInfo(DocumentResponseDto document) {
        return new Object() {
            public String getId() { return document.getId(); }
            public String getTitle() { return document.getTitle(); }
            public String getFileType() { return document.getFileType(); }
            public Long getFileSize() { return document.getFileSize(); }
            public String getFormattedFileSize() { return formatFileSize(document.getFileSize()); }
            public Integer getDurationSeconds() { return document.getDurationSeconds(); }
            public String getFormattedDuration() { 
                return document.getDurationSeconds() != null ? formatDuration(document.getDurationSeconds()) : null; 
            }
            public Integer getViewCount() { return document.getViewCount(); }
            public Integer getDownloadCount() { return document.getDownloadCount(); }
            public Boolean getIsStreamable() { return isMediaFile(document.getFileType().toLowerCase()); }
            public String getStreamUrl() { return "/api/documents/stream/" + document.getId(); }
            public String getViewUrl() { return "/api/documents/view/" + document.getId(); }
            public String getDownloadUrl() { return "/api/documents/download/" + document.getId(); }
            public String getThumbnailUrl() { return "/api/documents/thumbnail/" + document.getId(); }
        };
    }

    private String formatFileSize(Long bytes) {
        if (bytes == null) return "Unknown";
        if (bytes < 1024) return bytes + " B";
        if (bytes < 1024 * 1024) return String.format("%.1f KB", bytes / 1024.0);
        if (bytes < 1024 * 1024 * 1024) return String.format("%.1f MB", bytes / (1024.0 * 1024.0));
        return String.format("%.1f GB", bytes / (1024.0 * 1024.0 * 1024.0));
    }

    private String formatDuration(Integer seconds) {
        if (seconds == null) return "Unknown";
        int hours = seconds / 3600;
        int minutes = (seconds % 3600) / 60;
        int secs = seconds % 60;
        
        if (hours > 0) {
            return String.format("%02d:%02d:%02d", hours, minutes, secs);
        } else {
            return String.format("%02d:%02d", minutes, secs);
        }
    }

    private String getBaseName(String filename) {
        int lastDot = filename.lastIndexOf('.');
        return lastDot > 0 ? filename.substring(0, lastDot) : filename;
    }
}
