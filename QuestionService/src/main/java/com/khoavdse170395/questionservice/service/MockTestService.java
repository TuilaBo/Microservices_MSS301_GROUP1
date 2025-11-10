package com.khoavdse170395.questionservice.service;

import com.khoavdse170395.questionservice.model.dto.request.MockTestRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockTestResponseDTO;

import java.util.List;

public interface MockTestService {
    MockTestResponseDTO create(MockTestRequestDTO dto);
    MockTestResponseDTO update(Long id, MockTestRequestDTO dto);
    void delete(Long id);
    MockTestResponseDTO getById(Long id);
    List<MockTestResponseDTO> getAll();
    List<MockTestResponseDTO> getByLessonId(String lessonId);
}
