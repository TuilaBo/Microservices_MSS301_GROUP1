package com.khoavdse170395.questionservice.repository;

import com.khoavdse170395.questionservice.model.MockAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockAnswerRepository extends JpaRepository<MockAnswer, Long> {
    java.util.Optional<MockAnswer> findFirstByMockAttempt_IdAndMockQuestion_Id(Long attemptId, Long questionId);
}

