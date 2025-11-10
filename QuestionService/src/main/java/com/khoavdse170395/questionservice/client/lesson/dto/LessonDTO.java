package com.khoavdse170395.questionservice.client.lesson.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LessonDTO {
    private String id;
    private String title;
    private String content;
    private Integer gradeLevel;
    private String lessonType;
    private Integer durationMinutes;
    private String objectives;
    private String methodology;
    private String materials;
    private String homework;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
