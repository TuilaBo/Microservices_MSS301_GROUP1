package com.khoavdse170395.questionservice.service;

import com.khoavdse170395.questionservice.model.dto.request.MockAnswerRequestDTO;
import com.khoavdse170395.questionservice.model.dto.request.MockAttemptRequestDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockAnswerSubmissionResultDTO;
import com.khoavdse170395.questionservice.model.dto.response.MockAttemptResponseDTO;

import java.util.List;

public interface MockAttemptService {
    MockAttemptResponseDTO create(MockAttemptRequestDTO dto);
    MockAttemptResponseDTO update(Long id, MockAttemptRequestDTO dto);
    void delete(Long id);
    MockAttemptResponseDTO getById(Long id);
    List<MockAttemptResponseDTO> getAll();

    // Add answer to an attempt within time window
    MockAnswerSubmissionResultDTO addAnswer(Long attemptId, MockAnswerRequestDTO answerDTO);

    // Finalize attempt after end time and compute total points
    MockAttemptResponseDTO finalizeAndGrade(Long attemptId);
    int finalizeExpiredAttempts();

    // Start an attempt for a given test and current user
    MockAttemptResponseDTO startAttempt(Long testId);

    // Get attempt by id for current user (ownership enforced)
    MockAttemptResponseDTO getMyAttemptById(Long id);

    // Get all attempts for current user
    List<MockAttemptResponseDTO> getMyAttempts();
}
