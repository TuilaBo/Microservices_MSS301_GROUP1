package com.khoavdse170395.documentservice.dto;

import lombok.Data;

@Data
public class DocumentRequestDto {
    private String title;
    private String description;
    private String category;
    private Integer gradeLevel;
    private String subject;
    private String tags;
    private Boolean isPublic = true;
}
