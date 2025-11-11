package com.khoavdse170395.questionservice.client;

import com.khoavdse170395.questionservice.client.dto.ai.GradingApiResponse;
import com.khoavdse170395.questionservice.client.dto.ai.QuestionAnswerRequest;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "ai-service", url = "${services.ai.base-url}")
public interface AIGradingClient {

    @PostMapping("/grading")
    GradingApiResponse gradeFRQForLiteratureSubject(@RequestBody QuestionAnswerRequest request);
}
