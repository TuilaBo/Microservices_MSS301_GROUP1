package com.khoavdse170395.questionservice.model.dto.response;

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
public class MockAnswerResponseDTO {
    private Long id;
    private Long accountId;
    private Integer answerPoint;
    private Integer maxPoint;
    private QuestionType questionType;
    private String answerText;
    private String comments;
    private Long mockOptionId;
    private Long mockQuestionId;
    private Long mockAttemptId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

