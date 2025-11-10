package com.khoavdse170395.questionservice.client.payment.dto;

import com.khoavdse170395.questionservice.model.MembershipStatus;
import com.khoavdse170395.questionservice.model.MembershipTier;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class MembershipDTO {
    private Long id;
    private Long userId;
    private MembershipTier tier;
    private MembershipStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Long pricePaid;
    private LocalDateTime createdAt;
}
