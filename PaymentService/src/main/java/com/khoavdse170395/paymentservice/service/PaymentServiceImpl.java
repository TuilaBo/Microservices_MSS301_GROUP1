package com.khoavdse170395.paymentservice.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.khoavdse170395.paymentservice.controller.dto.CreatePaymentRequest;
import com.khoavdse170395.paymentservice.controller.dto.CreatePaymentResponse;
import com.khoavdse170395.paymentservice.config.PaymentProps;
import com.khoavdse170395.paymentservice.domain.OrderEntity;
import com.khoavdse170395.paymentservice.domain.OutboxEvent;
import com.khoavdse170395.paymentservice.domain.PaymentEntity;
import com.khoavdse170395.paymentservice.repo.OrderRepo;
import com.khoavdse170395.paymentservice.repo.OutboxRepo;
import com.khoavdse170395.paymentservice.repo.PaymentRepo;
import com.khoavdse170395.paymentservice.util.PaymentSigner;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepo orderRepo;
    private final PaymentRepo paymentRepo;
    private final OutboxRepo outboxRepo;
    private final PaymentProps vnp;
    private final ObjectMapper om = new ObjectMapper();
    private final MembershipService membershipService;

    public PaymentServiceImpl(OrderRepo orderRepo, PaymentRepo paymentRepo, OutboxRepo outboxRepo, PaymentProps vnp, MembershipService membershipService){
        this.orderRepo = orderRepo;
        this.paymentRepo = paymentRepo;
        this.outboxRepo = outboxRepo;
        this.vnp = vnp;
        this.membershipService = membershipService;
    }

    @Override
    @Transactional
    public CreatePaymentResponse createCheckout(CreatePaymentRequest req, String clientIp){
        OrderEntity order = orderRepo.findById(req.getOrderId()).orElseThrow();
        if (!"CREATED".equals(order.getStatus()) && !"PENDING".equals(order.getStatus()))
            throw new IllegalStateException("Order not payable");

        long amount = Optional.ofNullable(req.getAmountOverrideVnd()).orElse(order.getAmountVnd());

        PaymentEntity p = new PaymentEntity();
        p.setOrderId(order.getId());
        p.setProvider("VNPAY");
        p.setTxnRef(String.valueOf(System.currentTimeMillis()));
        p.setAmountVnd(amount);
        p.setCurrency("VND");
        p.setStatus("PENDING");
        p.setCreatedAt(new Timestamp(System.currentTimeMillis()));
        p.setUpdatedAt(p.getCreatedAt());
        paymentRepo.save(p);

        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyyMMddHHmmss").withZone(ZoneId.of("Asia/Ho_Chi_Minh"));
        String create = fmt.format(Instant.now());
        String expire = fmt.format(Instant.now().plus(15, ChronoUnit.MINUTES));

        Map<String,String> params = new LinkedHashMap<>();
        params.put("vnp_Version","2.1.0");
        params.put("vnp_Command","pay");
        params.put("vnp_TmnCode", vnp.getTmnCode());
        params.put("vnp_Amount", String.valueOf(amount*100));
        params.put("vnp_CurrCode","VND");
        params.put("vnp_TxnRef", p.getTxnRef());
        params.put("vnp_OrderInfo", order.getTitle() != null ? order.getTitle() : "Order " + order.getId());
        params.put("vnp_OrderType", "other");
        params.put("vnp_Locale","vn");
        params.put("vnp_ReturnUrl", vnp.getReturnUrl());
        params.put("vnp_IpAddr", clientIp);
        params.put("vnp_CreateDate", create);
        params.put("vnp_ExpireDate", expire);

        String redirectUrl = vnp.getPayUrl() + "?" + PaymentSigner.buildSignedQuery(params, vnp.getHashSecret());

        order.setStatus("PENDING");
        orderRepo.save(order);

        return new CreatePaymentResponse(p.getId(), p.getTxnRef(), redirectUrl);
    }

    @Override
    @Transactional
    public void markSucceeded(String txnRef, String transactionNo){
        PaymentEntity p = paymentRepo.findByTxnRef(txnRef).orElseThrow();
        if ("SUCCEEDED".equals(p.getStatus())) return;
        p.setStatus("SUCCEEDED");
        p.setExternalTxnId(transactionNo);
        p.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        paymentRepo.save(p);

        OrderEntity order = orderRepo.findById(p.getOrderId()).orElseThrow();
        order.setStatus("PAID");
        orderRepo.save(order);

        // Activate any membership linked to this txnRef (paymentReference)
        try {
            membershipService.activateMembershipByPaymentReference(txnRef, p.getAmountVnd());
        } catch (Exception ex) {
            // log and continue; membership activation failure shouldn't block payment processing
            // (Assuming we have a logger; keep simple here)
            System.err.println("Failed to activate membership for txnRef=" + txnRef + ": " + ex.getMessage());
        }

        emitOutbox("PAYMENT_SUCCEEDED","Payment", p.getId(), Map.of(
                "orderId", order.getId(),
                "amountVnd", p.getAmountVnd(),
                "txnRef", p.getTxnRef(),
                "externalTxnId", p.getExternalTxnId()
        ));
    }

    @Override
    @Transactional
    public void markFailed(String txnRef, String reason){
        PaymentEntity p = paymentRepo.findByTxnRef(txnRef).orElseThrow();
        if ("SUCCEEDED".equals(p.getStatus())) return;
        p.setStatus("FAILED");
        p.setReason(reason);
        p.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        paymentRepo.save(p);

        OrderEntity order = orderRepo.findById(p.getOrderId()).orElseThrow();
        order.setStatus("FAILED");
        orderRepo.save(order);

        emitOutbox("PAYMENT_FAILED","Payment", p.getId(), Map.of(
                "orderId", order.getId(),
                "reason", reason,
                "txnRef", p.getTxnRef()
        ));
    }

    @Override
    @Transactional
    public void cancelPayment(String txnRef) {
        PaymentEntity p = paymentRepo.findByTxnRef(txnRef).orElseThrow(
                () -> new RuntimeException("Payment not found with txnRef=" + txnRef)
        );

        // Don't cancel if already succeeded
        if ("SUCCEEDED".equals(p.getStatus())) {
            throw new IllegalStateException("Cannot cancel succeeded payment");
        }

        // Update payment status to CANCELED
        p.setStatus("CANCELED");
        p.setReason("User canceled payment");
        p.setUpdatedAt(new Timestamp(System.currentTimeMillis()));
        paymentRepo.save(p);

        // Update order status to CANCELED
        OrderEntity order = orderRepo.findById(p.getOrderId()).orElseThrow();
        order.setStatus("CANCELED");
        orderRepo.save(order);

        // Emit outbox event
        emitOutbox("PAYMENT_CANCELED", "Payment", p.getId(), Map.of(
                "orderId", order.getId(),
                "txnRef", p.getTxnRef(),
                "reason", "User canceled"
        ));
    }

    private void emitOutbox(String type, String aggType, Integer aggId, Map<String,Object> payload){
        try{
            OutboxEvent ev = new OutboxEvent();
            ev.setType(type);
            ev.setAggregateType(aggType);
            ev.setAggregateId(aggId);
            ev.setPayload(om.writeValueAsString(payload));
            ev.setPublished(false);
            outboxRepo.save(ev);
        }catch(Exception e){
            throw new RuntimeException(e);
        }
    }
}
