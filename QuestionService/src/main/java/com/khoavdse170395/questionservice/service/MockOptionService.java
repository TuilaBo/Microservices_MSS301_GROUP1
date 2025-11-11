package com.khoavdse170395.questionservice.service;

import com.khoavdse170395.questionservice.model.dto.request.MockOptionRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockOptionResponseDTO;

import java.util.List;

public interface MockOptionService {
    List<MockOptionResponseDTO> getByQuestionId(Long questionId);
    MockOptionResponseDTO createForQuestion(Long questionId, MockOptionRequestDTO dto);
    MockOptionResponseDTO updateForQuestion(Long questionId, Long optionId, MockOptionRequestDTO dto);
}
