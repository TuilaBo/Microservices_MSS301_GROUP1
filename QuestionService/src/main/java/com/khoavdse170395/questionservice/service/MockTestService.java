package com.khoavdse170395.questionservice.service;

import com.khoavdse170395.questionservice.model.dto.MockTestDTO;

import java.util.List;

public interface MockTestService {
    MockTestDTO create(MockTestDTO dto);
    MockTestDTO update(Long id, MockTestDTO dto);
    void delete(Long id);
    MockTestDTO getById(Long id);
    List<MockTestDTO> getAll();
}

