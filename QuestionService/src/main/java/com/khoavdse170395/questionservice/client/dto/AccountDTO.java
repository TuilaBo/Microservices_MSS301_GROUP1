package com.khoavdse170395.questionservice.client.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class AccountDTO {
    private Long userId;
    private String userName;
    private String email;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String fullName;
    private String gender;
    private LocalDate birthday;
    private String grade;
}

