package com.khoavdse170395.questionservice.repository;

import com.khoavdse170395.questionservice.model.AttemptStatus;
import com.khoavdse170395.questionservice.model.MockAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MockAttemptRepository extends JpaRepository<MockAttempt, Long> {
    Optional<MockAttempt> findFirstByUserSubscriptionIdAndMockTest_IdAndStatus(Long userSubscriptionId, Long mockTestId, AttemptStatus status);
}

