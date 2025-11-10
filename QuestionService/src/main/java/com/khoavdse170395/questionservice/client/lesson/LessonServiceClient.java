package com.khoavdse170395.questionservice.client.lesson;

import com.khoavdse170395.questionservice.client.lesson.dto.LessonApiResponse;
import com.khoavdse170395.questionservice.client.lesson.dto.LessonDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "lesson-service", url = "${services.lesson.base-url}")
public interface LessonServiceClient {

    @GetMapping("/api/lessons/{lessonId}")
    LessonApiResponse<LessonDTO> getLessonById(@PathVariable("lessonId") String lessonId);
}
