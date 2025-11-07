package com.khoavdse170395.accountservice.repository;

import com.khoavdse170395.accountservice.model.Account;
import com.khoavdse170395.accountservice.model.VerificationCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface VerificationCodeRepository extends JpaRepository<VerificationCode, Long> {
    Optional<VerificationCode> findByCodeAndAccountAndIsUsedFalseAndExpiresAtAfter(
            String code, Account account, LocalDateTime now);
    
    Optional<VerificationCode> findFirstByAccountOrderByCreatedAtDesc(Account account);
    
    void deleteByAccount(Account account);
}

