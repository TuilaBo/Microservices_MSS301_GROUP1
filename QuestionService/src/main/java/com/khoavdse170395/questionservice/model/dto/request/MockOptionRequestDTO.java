package com.khoavdse170395.questionservice.model.dto.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request payload for creating or updating mock options.
 * Relationship fields are represented as identifiers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockOptionRequestDTO {
    private String name;
    private boolean answer;
    private Long questionId;
}
