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
public class MockTestDTO {
    private Long id;
    private String name;
    private Long durationSeconds;
    private Integer totalPoint;
    private Long lessonPlanId;
    private List<Long> subscriptionPackageIds;
    private List<MockQuestionDTO> questions;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
