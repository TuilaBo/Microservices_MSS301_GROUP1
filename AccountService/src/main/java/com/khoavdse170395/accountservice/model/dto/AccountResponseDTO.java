package com.khoavdse170395.accountservice.model.dto;

import com.khoavdse170395.accountservice.model.Gender;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    
    private Long userId;
    private String userName;
    private String email;
    private String role;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private String fullName;
    private Gender gender;
    private LocalDate birthday;
    private String grade;
}
