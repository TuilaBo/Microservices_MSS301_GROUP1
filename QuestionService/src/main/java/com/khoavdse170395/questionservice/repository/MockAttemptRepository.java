package com.khoavdse170395.questionservice.repository;

import com.khoavdse170395.questionservice.model.AttemptStatus;
import com.khoavdse170395.questionservice.model.MockAttempt;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MockAttemptRepository extends JpaRepository<MockAttempt, Long> {
    Optional<MockAttempt> findFirstByUserIdAndMockTest_IdAndStatus(Long userId, Long mockTestId, AttemptStatus status);
    List<MockAttempt> findByUserId(Long userId);
    List<MockAttempt> findByStatusAndEndTimeBefore(AttemptStatus status, LocalDateTime before);
}

