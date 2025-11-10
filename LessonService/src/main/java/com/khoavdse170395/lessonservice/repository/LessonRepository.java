package com.khoavdse170395.lessonservice.repository;

import com.khoavdse170395.lessonservice.entity.Lesson;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LessonRepository extends MongoRepository<Lesson, String> {


    List<Lesson> findByGradeLevel(Integer gradeLevel);


    List<Lesson> findByLessonType(String lessonType);


    @Query("{'$or': [{'title': {'$regex': ?0, '$options': 'i'}}, {'content': {'$regex': ?0, '$options': 'i'}}]}")
    List<Lesson> findByTitleOrContentContaining(String keyword);

    List<Lesson> findByGradeLevelAndLessonType(Integer gradeLevel, String lessonType);

    List<Lesson> findByDurationMinutesBetween(Integer minDuration, Integer maxDuration);
}
