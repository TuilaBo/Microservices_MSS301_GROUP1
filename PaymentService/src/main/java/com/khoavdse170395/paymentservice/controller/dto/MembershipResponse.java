package com.khoavdse170395.paymentservice.controller.dto;

import com.khoavdse170395.paymentservice.domain.MembershipStatus;
import com.khoavdse170395.paymentservice.domain.MembershipTier;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MembershipResponse {
    private Long id;
    private Long userId;
    private MembershipTier tier;
    private MembershipStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long pricePaid;
    private LocalDateTime createdAt;
}
