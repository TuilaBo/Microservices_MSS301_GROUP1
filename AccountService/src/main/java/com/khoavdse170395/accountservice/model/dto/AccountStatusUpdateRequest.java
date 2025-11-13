package com.khoavdse170395.accountservice.model.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class AccountStatusUpdateRequest {
    @NotNull
    private Boolean active;
}


