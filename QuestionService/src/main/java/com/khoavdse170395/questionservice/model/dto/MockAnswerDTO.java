package com.khoavdse170395.questionservice.model.dto;

import com.khoavdse170395.questionservice.model.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockAnswerDTO {
    private Long id;
    private Long accountId;
    private Integer answerPoint;
    private QuestionType questionType;
    private String answerText;
    private Long mockOptionId;
    private Long mockQuestionId;
    private Long mockAttemptId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

