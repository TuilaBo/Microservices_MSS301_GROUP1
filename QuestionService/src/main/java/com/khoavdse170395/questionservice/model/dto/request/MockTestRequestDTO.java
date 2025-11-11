package com.khoavdse170395.questionservice.model.dto.request;

import com.khoavdse170395.questionservice.model.MembershipTier;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request payload for creating or updating mock tests.
 * Relationship fields are represented as identifiers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockTestRequestDTO {
    private String name;
    private Long durationSeconds;
    private Integer totalPoint;
    private String lessonId;
    private MembershipTier requiredTier;
    private List<Long> questionIds;
}
