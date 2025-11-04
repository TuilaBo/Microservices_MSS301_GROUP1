package com.khoavdse170395.lessonservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonCreateRequest {
    private String title;
    private String content;
    private Integer gradeLevel;
    private String lessonType;
    private Integer durationMinutes;
    private String objectives;
    private String methodology;
    private String materials;
    private String homework;
}
