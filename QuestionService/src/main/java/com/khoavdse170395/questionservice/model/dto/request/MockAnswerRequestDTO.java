package com.khoavdse170395.questionservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating or updating mock answers.
 * Relationship fields are represented as identifiers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockAnswerRequestDTO {
    private Long accountId;
    private String answerText;
    private Long mockOptionId;
    private Long mockQuestionId;
    private Long mockAttemptId;
}
