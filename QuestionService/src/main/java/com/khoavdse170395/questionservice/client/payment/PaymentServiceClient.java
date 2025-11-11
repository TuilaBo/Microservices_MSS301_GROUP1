package com.khoavdse170395.questionservice.client.payment;

import com.khoavdse170395.questionservice.client.payment.dto.MembershipDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(name = "payment-service", url = "${services.payment.base-url}")
public interface PaymentServiceClient {

    @GetMapping("/api/memberships/user/{userId}")
    List<MembershipDTO> getMembershipsByUserId(@PathVariable("userId") Long userId);
}
