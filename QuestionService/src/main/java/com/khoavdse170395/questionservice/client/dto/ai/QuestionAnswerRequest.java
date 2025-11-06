package com.khoavdse170395.questionservice.client.dto.ai;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionAnswerRequest {
    private String question;
    private String answer;
}

