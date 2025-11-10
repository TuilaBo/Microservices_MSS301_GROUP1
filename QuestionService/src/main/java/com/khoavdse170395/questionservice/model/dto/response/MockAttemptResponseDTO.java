package com.khoavdse170395.questionservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import com.khoavdse170395.questionservice.model.AttemptStatus;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockAttemptResponseDTO {
    private Long id;
    private Long userId;
    private Long userSubscriptionId;
    private Integer attemptPoint;
    private Integer maxPoint;
    private Duration duration;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Long mockTestId;
    private AttemptStatus status;
    private List<MockAnswerResponseDTO> mockAnswers;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
