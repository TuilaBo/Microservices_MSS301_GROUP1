package com.khoavdse170395.questionservice.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockAttemptDTO {
    private Long id;
    private Long userSubscriptionId;
    private Integer attemptPoint;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<MockAnswerDTO> mockAnswers;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
