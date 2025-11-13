package com.khoavdse170395.accountservice.repository;

import com.khoavdse170395.accountservice.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    Optional<PasswordResetToken> findByToken(String token);
    void deleteByAccount_UserId(Long userId);
}


