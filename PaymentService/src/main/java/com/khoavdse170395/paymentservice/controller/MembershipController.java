package com.khoavdse170395.paymentservice.controller;

import com.khoavdse170395.paymentservice.controller.dto.CreateMembershipRequest;
import com.khoavdse170395.paymentservice.controller.dto.MembershipResponse;
import com.khoavdse170395.paymentservice.controller.dto.UpdateMembershipRequest;
import com.khoavdse170395.paymentservice.domain.OrderEntity;
import com.khoavdse170395.paymentservice.repo.OrderRepo;
import com.khoavdse170395.paymentservice.service.MembershipServiceImpl;
import com.khoavdse170395.paymentservice.service.PaymentServiceImpl;
import com.khoavdse170395.paymentservice.controller.dto.CreatePaymentRequest;
import com.khoavdse170395.paymentservice.controller.dto.CreatePaymentResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/memberships")
public class MembershipController {

    private final MembershipServiceImpl membershipService;
    private final OrderRepo orderRepo;
    private final PaymentServiceImpl paymentService;

    public MembershipController(MembershipServiceImpl membershipService, OrderRepo orderRepo, PaymentServiceImpl paymentService) {
        this.membershipService = membershipService;
        this.orderRepo = orderRepo;
        this.paymentService = paymentService;
    }

    // If client sends paymentReference (admin/seed), create directly. If not, treat as purchase: create Order + checkout and return payment info with created membership.
    @PostMapping
    public ResponseEntity<?> create(@Valid @RequestBody CreateMembershipRequest req, HttpServletRequest http) {
        // If paymentReference is missing -> create order and start checkout
        if (req.getPaymentReference() == null || req.getPaymentReference().isBlank()) {
            // determine price by tier
            long price = switch (req.getTier()) {
                case BASIC -> 10_000L;
                case SILVER -> 30_000L;
                case GOLD -> 50_000L;
                case PLATINUM -> 100_000L;
            };

            // create order
            OrderEntity order = new OrderEntity();
            order.setUserId(req.getUserId().longValue());
            order.setItemType("MEMBERSHIP");
            // DB schema requires item_id NOT NULL; use 0 as sentinel for membership (no specific item id)
            order.setItemId(0);
            order.setTitle("Membership " + req.getTier().name());
            order.setAmountVnd(price);
            order.setStatus("PENDING");
            order.setUserId(req.getUserId().longValue());
            orderRepo.save(order);

            // determine client IP
            String xff = http.getHeader("X-Forwarded-For");
            String ip;
            if (xff != null && !xff.isEmpty()) {
                int comma = xff.indexOf(',');
                ip = (comma > 0) ? xff.substring(0, comma).trim() : xff.trim();
            } else {
                ip = http.getRemoteAddr();
            }

            // create checkout
            CreatePaymentRequest payReq = new CreatePaymentRequest();
            payReq.setOrderId(order.getId());
            CreatePaymentResponse payResp = paymentService.createCheckout(payReq, ip);

            // create pending membership linked to txnRef
            CreateMembershipRequest memReq = new CreateMembershipRequest();
            memReq.setUserId(req.getUserId());
            memReq.setTier(req.getTier());
            memReq.setPaymentReference(payResp.getTxnRef());
            MembershipResponse membership = membershipService.createMembership(memReq);

            Map<String, Object> resp = new HashMap<>();
            resp.put("redirectUrl", payResp.getRedirectUrl());
            resp.put("txnRef", payResp.getTxnRef());
            resp.put("paymentId", payResp.getPaymentId());
            resp.put("membership", membership);

            return new ResponseEntity<>(resp, HttpStatus.CREATED);
        }

        // Otherwise create directly (admin/seed)
        MembershipResponse created = membershipService.createMembership(req);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @PostMapping("/purchase")
    public ResponseEntity<Map<String, Object>> purchase(@Valid @RequestBody CreateMembershipRequest req, HttpServletRequest http) {
        // determine price by tier
        long price = switch (req.getTier()) {
            case BASIC -> 10_000L;
            case SILVER -> 30_000L;
            case GOLD -> 50_000L;
            case PLATINUM -> 100_000L;
        };

        // create order
        OrderEntity order = new OrderEntity();
        order.setUserId(req.getUserId().longValue());
        order.setItemType("MEMBERSHIP");
        // DB schema requires item_id NOT NULL; use 0 as sentinel for membership (no specific item id)
        order.setItemId(0);
        order.setTitle("Membership " + req.getTier().name());
        order.setAmountVnd(price);
        order.setStatus("CREATED");
        orderRepo.save(order);

        // determine client IP similar to PaymentController
        String xff = http.getHeader("X-Forwarded-For");
        String ip;
        if (xff != null && !xff.isEmpty()) {
            int comma = xff.indexOf(',');
            ip = (comma > 0) ? xff.substring(0, comma).trim() : xff.trim();
        } else {
            ip = http.getRemoteAddr();
        }

        // create checkout
        CreatePaymentRequest payReq = new CreatePaymentRequest();
        payReq.setOrderId(order.getId());
        CreatePaymentResponse payResp = paymentService.createCheckout(payReq, ip);

        // create pending membership linked to txnRef
        CreateMembershipRequest memReq = new CreateMembershipRequest();
        memReq.setUserId(req.getUserId());
        memReq.setTier(req.getTier());
        memReq.setPaymentReference(payResp.getTxnRef());
        MembershipResponse membership = membershipService.createMembership(memReq);

        Map<String, Object> resp = new HashMap<>();
        resp.put("redirectUrl", payResp.getRedirectUrl());
        resp.put("txnRef", payResp.getTxnRef());
        resp.put("paymentId", payResp.getPaymentId());
        resp.put("membership", membership);

        return new ResponseEntity<>(resp, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembershipResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(membershipService.getMembership(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<MembershipResponse>> getForUser(@PathVariable Long userId) {
        return ResponseEntity.ok(membershipService.getMembershipsForUser(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<MembershipResponse> update(@PathVariable Long id, @Valid @RequestBody UpdateMembershipRequest req) {
        return ResponseEntity.ok(membershipService.updateMembership(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        membershipService.deleteMembership(id);
        return ResponseEntity.noContent().build();
    }

    // Admin / utility endpoints
    @GetMapping
    public ResponseEntity<List<MembershipResponse>> getAll() {
        return ResponseEntity.ok(membershipService.getAllMemberships());
    }

    @GetMapping("/by-payment/{paymentReference}")
    public ResponseEntity<MembershipResponse> getByPaymentReference(@PathVariable String paymentReference) {
        return ResponseEntity.ok(membershipService.getByPaymentReference(paymentReference));
    }

    @PostMapping("/activate/{paymentReference}")
    public ResponseEntity<Void> activateByPaymentReference(@PathVariable String paymentReference,
                                                           @RequestParam(required = false) Long amountVnd) {
        membershipService.activateMembershipByPaymentReference(paymentReference, amountVnd);
        return ResponseEntity.noContent().build();
    }
}
