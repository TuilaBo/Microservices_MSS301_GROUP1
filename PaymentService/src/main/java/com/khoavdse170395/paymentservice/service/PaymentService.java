package com.khoavdse170395.paymentservice.service;


import com.khoavdse170395.paymentservice.controller.dto.CreatePaymentRequest;
import com.khoavdse170395.paymentservice.controller.dto.CreatePaymentResponse;

public interface PaymentService {

    CreatePaymentResponse createCheckout(CreatePaymentRequest req, String clientIp);

    void markSucceeded(String txnRef, String transactionNo);

    void markFailed(String txnRef, String reason);
}
