package com.khoavdse170395.paymentservice.repo;

import com.khoavdse170395.paymentservice.domain.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PaymentRepo extends JpaRepository<PaymentEntity, Integer> {
    Optional<PaymentEntity> findByTxnRef(String txnRef);
}
