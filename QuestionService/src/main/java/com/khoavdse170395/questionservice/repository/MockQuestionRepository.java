package com.khoavdse170395.questionservice.repository;

import com.khoavdse170395.questionservice.model.MockQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockQuestionRepository extends JpaRepository<MockQuestion, Long> {
}

