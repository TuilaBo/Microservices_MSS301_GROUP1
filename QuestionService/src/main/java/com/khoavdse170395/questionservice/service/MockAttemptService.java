package com.khoavdse170395.questionservice.service;

import com.khoavdse170395.questionservice.model.dto.MockAttemptDTO;

import java.util.List;

public interface MockAttemptService {
    MockAttemptDTO create(MockAttemptDTO dto);
    MockAttemptDTO update(Long id, MockAttemptDTO dto);
    void delete(Long id);
    MockAttemptDTO getById(Long id);
    List<MockAttemptDTO> getAll();
}

