package com.khoavdse170395.questionservice.client.lesson.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LessonApiResponse<T> {
    private String message;
    private T data;
}
