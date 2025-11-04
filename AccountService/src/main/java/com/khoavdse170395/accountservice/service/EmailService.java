package com.khoavdse170395.accountservice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendVerificationCode(String email, String code) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(email);
            message.setSubject("Xác minh tài khoản - Account Service");
            message.setText("Mã xác minh của bạn là: " + code + "\n\nMã này có hiệu lực trong 15 phút.\n\nVui lòng không chia sẻ mã này với bất kỳ ai.");
            
            mailSender.send(message);
            log.info("Verification code sent to: {}", email);
        } catch (Exception e) {
            log.error("Failed to send verification email to: {}", email, e);
            throw new RuntimeException("Failed to send verification email", e);
        }
    }
}
