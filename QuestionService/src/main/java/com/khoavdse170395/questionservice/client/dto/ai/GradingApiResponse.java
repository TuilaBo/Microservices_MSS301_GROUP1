package com.khoavdse170395.questionservice.client.dto.ai;

import lombok.Data;

@Data
public class GradingApiResponse {
    private int code;
    private String message;
    private GradingResponse data;
}

