package com.khoavdse170395.questionservice.service;

import com.khoavdse170395.questionservice.model.dto.MockAnswerDTO;
import com.khoavdse170395.questionservice.model.dto.MockAttemptDTO;

import java.util.List;

public interface MockAttemptService {
    MockAttemptDTO create(MockAttemptDTO dto);
    MockAttemptDTO update(Long id, MockAttemptDTO dto);
    void delete(Long id);
    MockAttemptDTO getById(Long id);
    List<MockAttemptDTO> getAll();

    // Add answer to an attempt within time window
    MockAnswerDTO addAnswer(Long attemptId, com.khoavdse170395.questionservice.model.dto.MockAnswerDTO answerDTO);

    // Finalize attempt after end time and compute total points
    MockAttemptDTO finalizeAndGrade(Long attemptId);

    // Start an attempt for a given test and current user
    MockAttemptDTO startAttempt(Long testId);

    // Get attempt by id for current user (ownership enforced)
    MockAttemptDTO getMyAttemptById(Long id);
}
