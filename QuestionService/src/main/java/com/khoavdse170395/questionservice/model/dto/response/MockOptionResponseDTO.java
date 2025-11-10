package com.khoavdse170395.questionservice.model.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockOptionResponseDTO {
    private Long id;
    private String name;
    private boolean answer;
    private Long questionId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}

