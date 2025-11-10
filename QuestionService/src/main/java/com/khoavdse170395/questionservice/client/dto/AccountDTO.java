package com.khoavdse170395.questionservice.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class AccountDTO {
    private Long userId;
    private String username;
    private String email;
    private RoleDTO role;
    private Boolean active;
    private LocalDateTime createdAt;
    private String fullName;
    private String gender;
    private LocalDate birthday;
    private String grade;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RoleDTO {
        private Long roleId;
        private String roleName;
    }
}
