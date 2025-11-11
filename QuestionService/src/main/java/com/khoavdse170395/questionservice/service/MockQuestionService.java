package com.khoavdse170395.questionservice.service;

import com.khoavdse170395.questionservice.model.dto.request.MockQuestionRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockQuestionResponseDTO;

import java.util.List;

public interface MockQuestionService {
    MockQuestionResponseDTO create(MockQuestionRequestDTO dto);
    MockQuestionResponseDTO update(Long id, MockQuestionRequestDTO dto);
    void delete(Long id);
    MockQuestionResponseDTO getById(Long id);
    List<MockQuestionResponseDTO> getAll();
}

