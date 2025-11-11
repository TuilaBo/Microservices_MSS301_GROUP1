package com.khoavdse170395.questionservice.repository;

import com.khoavdse170395.questionservice.model.MockOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MockOptionRepository extends JpaRepository<MockOption, Long> {
    List<MockOption> findByQuestion_Id(Long questionId);
}

