package com.khoavdse170395.lessonservice.service;

import com.khoavdse170395.lessonservice.entity.Lesson;

import java.util.List;
import java.util.Optional;

public interface LessonService {

    // Basic CRUD operations
    List<Lesson> getAllLessons();
    Optional<Lesson> getLessonById(String id);
    Lesson createLesson(Lesson lesson);
    Lesson updateLesson(String id, Lesson lessonDetails);
    boolean deleteLesson(String id);

    // Advanced search operations
    List<Lesson> getLessonsByGradeLevel(Integer gradeLevel);
    List<Lesson> getLessonsByType(String lessonType);
    List<Lesson> searchLessons(String keyword);
    List<Lesson> getLessonsByGradeLevelAndType(Integer gradeLevel, String lessonType);
    List<Lesson> getLessonsByDurationRange(Integer minDuration, Integer maxDuration);
}
