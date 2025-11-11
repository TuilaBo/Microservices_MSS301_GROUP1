package com.khoavdse170395.questionservice.model.dto.request;

import com.khoavdse170395.questionservice.model.AttemptStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Request payload for creating or updating mock attempts.
 * Relationship fields are represented as identifiers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockAttemptRequestDTO {
    private Long userId;
    private Long userSubscriptionId;
    private Integer attemptPoint;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long mockTestId;
    private AttemptStatus status;
    private List<Long> mockAnswerIds;
}
