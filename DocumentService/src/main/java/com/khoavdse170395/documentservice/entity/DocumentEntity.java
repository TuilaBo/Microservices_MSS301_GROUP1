package com.khoavdse170395.documentservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "documents")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentEntity {

    @Id
    private String id;

    private String title;

    private String description;

    private String fileName;

    private String fileType; // PDF, DOCX, MP4, MP3, etc.

    private String fileUrl; // URL hoặc đường dẫn file

    private Long fileSize; // Kích thước file (bytes)

    private String category; // Hướng dẫn, Giáo trình, Bài tập, Video, Audio, Tham khảo

    private Integer gradeLevel; // Lớp học

    private String subject; // Môn học

    private String uploadedBy; // ID của user upload

    private Integer downloadCount; // Số lần download

    private Integer viewCount; // Số lần xem

    private String thumbnailUrl; // URL ảnh thumbnail (cho video, PDF)

    private Integer durationSeconds; // Thời lượng (cho video/audio)

    private String tags; // Tags phân cách bằng dấu phẩy

    private Boolean isPublic; // Công khai hay riêng tư

    private Boolean isActive; // Trạng thái hoạt động

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;
}
