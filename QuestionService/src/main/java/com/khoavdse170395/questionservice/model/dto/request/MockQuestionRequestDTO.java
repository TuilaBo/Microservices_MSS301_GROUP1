package com.khoavdse170395.questionservice.model.dto.request;

import com.khoavdse170395.questionservice.model.QuestionType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Request payload for creating or updating mock questions.
 * Relationship fields are represented as identifiers.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MockQuestionRequestDTO {
    private String question;
    private Integer point;
    private QuestionType questionType;
    private Long testId;
    private List<Long> optionIds;
    private Long answerId;
}
