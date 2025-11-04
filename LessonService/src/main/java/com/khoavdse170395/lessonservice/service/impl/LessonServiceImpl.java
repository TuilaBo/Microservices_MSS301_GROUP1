package com.khoavdse170395.lessonservice.service.impl;

import com.khoavdse170395.lessonservice.entity.Lesson;
import com.khoavdse170395.lessonservice.repository.LessonRepository;
import com.khoavdse170395.lessonservice.service.LessonService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LessonServiceImpl implements LessonService {

    private final LessonRepository lessonRepository;

    @Override
    public List<Lesson> getAllLessons() {
        return lessonRepository.findAll();
    }

    @Override
    public Optional<Lesson> getLessonById(String id) {
        return lessonRepository.findById(id);
    }

    @Override
    public Lesson createLesson(Lesson lesson) {
        return lessonRepository.save(lesson);
    }

    @Override
    public Lesson updateLesson(String id, Lesson lessonDetails) {
        Optional<Lesson> optionalLesson = lessonRepository.findById(id);
        if (optionalLesson.isPresent()) {
            Lesson lesson = optionalLesson.get();
            lesson.setTitle(lessonDetails.getTitle());
            lesson.setContent(lessonDetails.getContent());
            lesson.setGradeLevel(lessonDetails.getGradeLevel());
            lesson.setLessonType(lessonDetails.getLessonType());
            lesson.setDurationMinutes(lessonDetails.getDurationMinutes());
            lesson.setObjectives(lessonDetails.getObjectives());
            lesson.setMethodology(lessonDetails.getMethodology());
            lesson.setMaterials(lessonDetails.getMaterials());
            lesson.setHomework(lessonDetails.getHomework());
            return lessonRepository.save(lesson);
        }
        return null;
    }

    @Override
    public boolean deleteLesson(String id) {
        if (lessonRepository.existsById(id)) {
            lessonRepository.deleteById(id);
            return true;
        }
        return false;
    }

    @Override
    public List<Lesson> getLessonsByGradeLevel(Integer gradeLevel) {
        return lessonRepository.findByGradeLevel(gradeLevel);
    }

    @Override
    public List<Lesson> getLessonsByType(String lessonType) {
        return lessonRepository.findByLessonType(lessonType);
    }

    @Override
    public List<Lesson> searchLessons(String keyword) {
        return lessonRepository.findByTitleOrContentContaining(keyword);
    }



    @Override
    public List<Lesson> getLessonsByGradeLevelAndType(Integer gradeLevel, String lessonType) {
        return lessonRepository.findByGradeLevelAndLessonType(gradeLevel, lessonType);
    }

    @Override
    public List<Lesson> getLessonsByDurationRange(Integer minDuration, Integer maxDuration) {
        return lessonRepository.findByDurationMinutesBetween(minDuration, maxDuration);
    }
}
