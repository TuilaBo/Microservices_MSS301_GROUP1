package com.khoavdse170395.questionservice.model.dto.response;

import com.khoavdse170395.questionservice.model.MembershipTier;
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
public class MockTestResponseDTO {
    private Long id;
    private String name;
    private Long durationSeconds;
    private Integer totalPoint;
    private String lessonId;
    private MembershipTier requiredTier;
    private List<MockQuestionResponseDTO> questions;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
