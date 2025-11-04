package com.khoavdse170395.paymentservice.controller.dto;

import com.khoavdse170395.paymentservice.domain.MembershipTier;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateMembershipRequest {
    @NotNull
    private Long userId;

    @NotNull
    private MembershipTier tier;

    // Optional: payment txn reference or method
    private String paymentReference;
}

