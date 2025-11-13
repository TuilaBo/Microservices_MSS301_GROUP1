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
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dangkhoavo10@gmail.com"); // Set from address
        message.setTo(email);
        message.setSubject("Xác minh tài khoản - Account Service");
        message.setText("Mã xác minh của bạn là: " + code + "\n\nMã này có hiệu lực trong 15 phút.\n\nVui lòng không chia sẻ mã này với bất kỳ ai.");

        mailSender.send(message);
        log.info("Verification code sent to: {}", email);
    }

    public void sendPasswordResetEmail(String email, String token) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("dangkhoavo10@gmail.com");
        message.setTo(email);
        message.setSubject("Đặt lại mật khẩu - Account Service");
        message.setText("""
                Bạn đã yêu cầu đặt lại mật khẩu cho tài khoản của mình.
                
                Mã đặt lại mật khẩu của bạn: %s
                
                Mã có hiệu lực trong 15 phút.
                Nếu bạn không yêu cầu đặt lại mật khẩu, hãy bỏ qua email này.
                """.formatted(token));
        mailSender.send(message);
        log.info("Password reset token sent to: {}", email);
    }
}

