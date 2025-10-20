package com.khoavdse170395.accountservice.model.dto;

import com.khoavdse170395.accountservice.model.Gender;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class AccountCreateRequest {
    @NotBlank(message = "username is required")
    @Size(min = 5, max = 20, message = "must be 5-20")
    private String username;

    @NotBlank(message = "fullName is required")
    @Size(min = 5, max = 50, message = "must be 5-50")
    private String fullName;

    @NotBlank(message = "password is required")
    private String password;

    @NotBlank(message = "email is required")
    @Email(message = "invalid email")
    private String email;

    private Gender gender;

    @Column(name = "birthday", nullable = false)
    private LocalDate birthday;
}
