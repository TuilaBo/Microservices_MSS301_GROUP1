package com.khoavdse170395.lessonservice.repository;

import com.khoavdse170395.lessonservice.entity.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {

    // Tìm kiếm theo lớp học
    List<Lesson> findByGradeLevel(Integer gradeLevel);

    // Loại bài học
    List<Lesson> findByLessonType(String lessonType);

    // Tìm kiếm theo tiêu đề hoặc nội dung (case-insensitive)
    @Query("{'$or': [{'title': {'$regex': ?0, '$options': 'i'}}, {'content': {'$regex': ?0, '$options': 'i'}}]}")
    List<Lesson> findByTitleOrContentContaining(String keyword);

    // Tìm kiếm theo cả lớp và loại bài học
    List<Lesson> findByGradeLevelAndLessonType(Integer gradeLevel, String lessonType);

    // Tìm kiếm bài học có thời lượng trong khoảng
    List<Lesson> findByDurationMinutesBetween(Integer minDuration, Integer maxDuration);
}
