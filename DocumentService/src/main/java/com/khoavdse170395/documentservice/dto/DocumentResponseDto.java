package com.khoavdse170395.documentservice.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DocumentResponseDto {
    private String id;
    private String title;
    private String description;
    private String fileName;
    private String fileType;
    private String fileUrl;
    private Long fileSize;
    private String category;
    private Integer gradeLevel;
    private String subject;
    private String uploadedBy;
    private Integer downloadCount;
    private Integer viewCount;
    private String thumbnailUrl;
    private Integer durationSeconds;
    private String tags;
    private Boolean isPublic;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
