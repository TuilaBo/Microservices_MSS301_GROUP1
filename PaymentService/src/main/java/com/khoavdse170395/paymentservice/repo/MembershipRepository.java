package com.khoavdse170395.paymentservice.repo;

import com.khoavdse170395.paymentservice.domain.MembershipEntity;
import com.khoavdse170395.paymentservice.domain.MembershipStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MembershipRepository extends JpaRepository<MembershipEntity, Long> {
    Optional<MembershipEntity> findFirstByUserIdAndStatusOrderByStartDateDesc(Long userId, MembershipStatus status);
    List<MembershipEntity> findByUserIdOrderByStartDateDesc(Long userId);
    Optional<MembershipEntity> findByPaymentReference(String paymentReference);
}
