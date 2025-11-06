package com.khoavdse170395.paymentservice.controller.dto;

import com.khoavdse170395.paymentservice.domain.MembershipStatus;
import com.khoavdse170395.paymentservice.domain.MembershipTier;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class UpdateMembershipRequest {
    private MembershipTier tier;
    private MembershipStatus status;
    @Min(1)
    private Long extendDays; // number of days to extend
}
