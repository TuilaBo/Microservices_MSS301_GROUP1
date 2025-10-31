package com.khoavdse170395.paymentservice.repo;


import com.khoavdse170395.paymentservice.domain.OutboxEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OutboxRepo extends JpaRepository<OutboxEvent, Integer> {
    List<OutboxEvent> findTop100ByPublishedFalseOrderByCreatedAtAsc();
}
