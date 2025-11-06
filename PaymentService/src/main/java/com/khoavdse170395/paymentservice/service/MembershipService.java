package com.khoavdse170395.paymentservice.service;

import com.khoavdse170395.paymentservice.controller.dto.CreateMembershipRequest;
import com.khoavdse170395.paymentservice.controller.dto.MembershipResponse;
import com.khoavdse170395.paymentservice.controller.dto.UpdateMembershipRequest;

import java.util.List;

public interface MembershipService {
    MembershipResponse createMembership(CreateMembershipRequest req);
    MembershipResponse getMembership(Long id);
    List<MembershipResponse> getMembershipsForUser(Long userId);
    MembershipResponse updateMembership(Long id, UpdateMembershipRequest req);
    void deleteMembership(Long id);
    void activateMembershipByPaymentReference(String paymentReference, Long amountVnd);
    List<MembershipResponse> getAllMemberships();
    MembershipResponse getByPaymentReference(String paymentReference);
}
