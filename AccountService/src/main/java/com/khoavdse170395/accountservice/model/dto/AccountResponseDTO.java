package com.khoavdse170395.accountservice.model.dto;

import com.khoavdse170395.accountservice.model.Gender;
import com.khoavdse170395.accountservice.model.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponseDTO {
    private Long userId;
    private String username;
    private String email;
    private Role role;
    private Boolean active;
    private LocalDateTime createdAt;
    private String fullName;
    private Gender gender;
    private LocalDate birthday;
    private String grade;
}