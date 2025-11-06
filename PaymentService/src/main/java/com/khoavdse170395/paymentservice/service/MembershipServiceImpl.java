package com.khoavdse170395.paymentservice.service;

import com.khoavdse170395.paymentservice.controller.dto.CreateMembershipRequest;
import com.khoavdse170395.paymentservice.controller.dto.MembershipResponse;
import com.khoavdse170395.paymentservice.controller.dto.UpdateMembershipRequest;
import com.khoavdse170395.paymentservice.domain.MembershipEntity;
import com.khoavdse170395.paymentservice.domain.MembershipStatus;
import com.khoavdse170395.paymentservice.domain.MembershipTier;
import com.khoavdse170395.paymentservice.exception.ResourceNotFoundException;
import com.khoavdse170395.paymentservice.repo.MembershipRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class MembershipServiceImpl implements MembershipService {

    private final MembershipRepository membershipRepository;

    public MembershipServiceImpl(MembershipRepository membershipRepository) {
        this.membershipRepository = membershipRepository;
    }

    @Override
    public MembershipResponse createMembership(CreateMembershipRequest req) {
        // If paymentReference provided, create PENDING membership and wait for payment confirmation
        boolean hasPaymentRef = req.getPaymentReference() != null && !req.getPaymentReference().isBlank();

        long price = hasPaymentRef ? 0L : priceForTier(req.getTier());
        int days = daysForTier(req.getTier());

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime end = hasPaymentRef ? null : now.plusDays(days);

        MembershipEntity e = MembershipEntity.builder()
                .userId(req.getUserId())
                .tier(req.getTier())
                .status(hasPaymentRef ? MembershipStatus.PENDING : MembershipStatus.ACTIVE)
                .startDate(hasPaymentRef ? null : now)
                .endDate(end)
                .pricePaid(price)
                .paymentReference(req.getPaymentReference())
                .build();

        MembershipEntity saved = membershipRepository.save(e);
        return toDto(saved);
    }

    @Override
    public MembershipResponse getMembership(Long id) {
        MembershipEntity e = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id=" + id));
        return toDto(e);
    }

    @Override
    public List<MembershipResponse> getMembershipsForUser(Long userId) {
        return membershipRepository.findByUserIdOrderByStartDateDesc(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MembershipResponse updateMembership(Long id, UpdateMembershipRequest req) {
        MembershipEntity e = membershipRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with id=" + id));

        if (req.getTier() != null && req.getTier() != e.getTier()) {
            long newPrice = priceForTier(req.getTier());
            int newDays = daysForTier(req.getTier());
            e.setTier(req.getTier());
            e.setPricePaid(newPrice);
            e.setStartDate(LocalDateTime.now());
            e.setEndDate(LocalDateTime.now().plusDays(newDays));
            e.setStatus(MembershipStatus.ACTIVE);
        }

        if (req.getStatus() != null) {
            e.setStatus(req.getStatus());
        }

        if (req.getExtendDays() != null && req.getExtendDays() > 0) {
            if (e.getEndDate() == null) e.setEndDate(LocalDateTime.now().plusDays(req.getExtendDays()));
            else e.setEndDate(e.getEndDate().plusDays(req.getExtendDays()));
        }

        MembershipEntity saved = membershipRepository.save(e);
        return toDto(saved);
    }

    @Override
    public void deleteMembership(Long id) {
        if (!membershipRepository.existsById(id)) {
            throw new ResourceNotFoundException("Membership not found with id=" + id);
        }
        membershipRepository.deleteById(id);
    }

    @Override
    public void activateMembershipByPaymentReference(String paymentReference, Long amountVnd) {
        if (paymentReference == null || paymentReference.isBlank()) return;
        Optional<MembershipEntity> opt = membershipRepository.findByPaymentReference(paymentReference);
        if (opt.isEmpty()) return; // no membership waiting for this payment

        MembershipEntity e = opt.get();
        if (e.getStatus() == MembershipStatus.ACTIVE) return;

        // activate membership: set start/end and pricePaid based on tier
        int days = daysForTier(e.getTier());
        LocalDateTime now = LocalDateTime.now();
        e.setStartDate(now);
        e.setEndDate(now.plusDays(days));
        e.setPricePaid(amountVnd != null ? amountVnd : priceForTier(e.getTier()));
        e.setStatus(MembershipStatus.ACTIVE);

        membershipRepository.save(e);
    }

    // New implementations for interface additions
    @Override
    public List<MembershipResponse> getAllMemberships() {
        return membershipRepository.findAll()
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public MembershipResponse getByPaymentReference(String paymentReference) {
        MembershipEntity e = membershipRepository.findByPaymentReference(paymentReference)
                .orElseThrow(() -> new ResourceNotFoundException("Membership not found with paymentReference=" + paymentReference));
        return toDto(e);
    }

    private MembershipResponse toDto(MembershipEntity e) {
        return new MembershipResponse(
                e.getId(),
                e.getUserId(),
                e.getTier(),
                e.getStatus(),
                e.getStartDate(),
                e.getEndDate(),
                e.getPricePaid(),
                e.getCreatedAt()
        );
    }

    private long priceForTier(MembershipTier tier) {
        return switch (tier) {
            case BASIC -> 10_000L;
            case SILVER -> 30_000L;
            case GOLD -> 50_000L;
            case PLATINUM -> 100_000L;
        };
    }

    private int daysForTier(MembershipTier tier) {
        return switch (tier) {
            case BASIC -> 30;
            case SILVER -> 90;
            case GOLD -> 180;
            case PLATINUM -> 365;
        };
    }
}
