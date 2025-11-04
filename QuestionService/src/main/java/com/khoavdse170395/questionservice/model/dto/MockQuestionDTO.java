package com.khoavdse170395.questionservice.model.dto;

import com.khoavdse170395.questionservice.model.QuestionType;
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
public class MockQuestionDTO {
    private Long id;
    private String question;
    private Integer point;
    private QuestionType questionType;
    private Long testId;
    private List<MockOptionDTO> options;
    private Long answerId;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}
