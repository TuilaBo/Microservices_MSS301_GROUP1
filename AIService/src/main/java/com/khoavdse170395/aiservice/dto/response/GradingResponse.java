package com.khoavdse170395.aiservice.dto.response;

public record GradingResponse(
        Double understanding,
        Double contentQuality,
        Double organization,
        Double expression,
        Double creativity,
        Double total,
        String feedback
) {

}
