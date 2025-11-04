package com.khoavdse170395.questionservice.service;

import com.khoavdse170395.questionservice.model.dto.MockQuestionDTO;

import java.util.List;

public interface MockQuestionService {
    MockQuestionDTO create(MockQuestionDTO dto);
    MockQuestionDTO update(Long id, MockQuestionDTO dto);
    void delete(Long id);
    MockQuestionDTO getById(Long id);
    List<MockQuestionDTO> getAll();
}

