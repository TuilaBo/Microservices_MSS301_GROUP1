package com.khoavdse170395.questionservice.client.dto.ai;

import lombok.Data;

@Data
public class GradingApiResponse {
    private Integer code;
    private String message;
    private GradingDetail data;

    @Data
    public static class GradingDetail {
        private Double understanding;
        private Double contentQuality;
        private Double organization;
        private Double expression;
        private Double creativity;
        private Double total;
        private String feedback;
    }
}
