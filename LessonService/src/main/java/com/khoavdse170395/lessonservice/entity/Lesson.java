package com.khoavdse170395.lessonservice.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "lessons")
public class Lesson {
    @Id
    private String id;

    private String title;
    private String content;
    private Integer gradeLevel; // Lớp (6, 7, 8, 9, 10, 11, 12)
    private String lessonType; // Loại bài (Văn học, Tiếng Việt, Tập làm văn)
    private Integer durationMinutes; // Thời lượng bài học (phút)
    private String objectives; // Mục tiêu bài học
    private String methodology; // Phương pháp giảng dạy
    private String materials; // Tài liệu cần thiết
    private String homework; // Bài tập về nhà

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public Lesson(String title, String content,Integer gradeLevel, String lessonType,
                  Integer durationMinutes, String objectives, String methodology,
                  String materials, String homework) {
        this.title = title;
        this.content = content;
        this.gradeLevel = gradeLevel;
        this.lessonType = lessonType;
        this.durationMinutes = durationMinutes;
        this.objectives = objectives;
        this.methodology = methodology;
        this.materials = materials;
        this.homework = homework;
    }
}
