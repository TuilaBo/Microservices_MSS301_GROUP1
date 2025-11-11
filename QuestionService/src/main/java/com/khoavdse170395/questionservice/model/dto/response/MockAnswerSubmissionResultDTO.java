package com.khoavdse170395.questionservice.model.dto.response;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class MockAnswerSubmissionResultDTO {
    MockAnswerResponseDTO answer;
    MockAttemptResponseDTO finalizedAttempt;
    boolean finalized;
    String message;
}
