package com.khoavdse170395.paymentservice.controller;


import com.khoavdse170395.paymentservice.controller.dto.CreatePaymentRequest;
import com.khoavdse170395.paymentservice.controller.dto.CreatePaymentResponse;
import com.khoavdse170395.paymentservice.config.PaymentProps;
import com.khoavdse170395.paymentservice.service.PaymentServiceImpl;
import com.khoavdse170395.paymentservice.util.PaymentSigner;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.util.*;

@Controller
@RequestMapping("/api/payments/vnpay")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentServiceImpl service;
    private final PaymentProps vnp;

    public PaymentController(PaymentServiceImpl service, PaymentProps vnp) {
        this.service = service;
        this.vnp = vnp;
    }

    @PostMapping("/create")
    public ResponseEntity<CreatePaymentResponse> create(@Valid @RequestBody CreatePaymentRequest req,
                                                        HttpServletRequest http) {
        // Tránh Optional/lambda để loại trừ lỗi do ký tự ẩn khi copy
        String xff = http.getHeader("X-Forwarded-For");
        String ip;
        if (xff != null && !xff.isEmpty()) {
            int comma = xff.indexOf(',');
            ip = (comma > 0) ? xff.substring(0, comma).trim() : xff.trim();
        } else {
            ip = http.getRemoteAddr();
        }
        CreatePaymentResponse resp = service.createCheckout(req, ip);
        return ResponseEntity.ok(resp);
    }

    // IPN (server-to-server)
    @PostMapping("/ipn")
    public ResponseEntity<String> ipn(HttpServletRequest request) {
        // Lấy toàn bộ tham số theo cách thuần để tránh lỗi lambda
        Map<String, String> params = new HashMap<>();
        Map<String, String[]> raw = request.getParameterMap();
        for (Map.Entry<String, String[]> e : raw.entrySet()) {
            if (e.getValue() != null && e.getValue().length > 0) {
                params.put(e.getKey(), e.getValue()[0]);
            }
        }
        Map<String, String> verifyMap = new HashMap<>(params);
        if (!PaymentSigner.verify(verifyMap, vnp.getHashSecret())) {
            return ResponseEntity.ok("{\"RspCode\":\"97\",\"Message\":\"Invalid signature\"}");
        }

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        String transactionNo = params.get("vnp_TransactionNo");

        if ("00".equals(responseCode)) {
            service.markSucceeded(txnRef, transactionNo);
            return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Success\"}");
        } else {
            service.markFailed(txnRef, "VNPay=" + responseCode);
            return ResponseEntity.ok("{\"RspCode\":\"00\",\"Message\":\"Confirm Failed Recorded\"}");
        }
    }
    // Return URL (user redirect) - Trả về HTML template
    @GetMapping("/return")
    public void vnpayReturn(@RequestParam Map<String, String> params, HttpServletResponse response) throws IOException {
        logger.info("Received VNPAY return callback with params: {}", params);

        // Validate secure hash
        if (!validateSecureHash(params)) {
            logger.error("Invalid signature for VNPAY return");
            String errorUrl = "http://localhost:5173/#payment-result?vnp_ResponseCode=97&error=invalid_signature";
            response.sendRedirect(errorUrl);
            return;
        }

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        String transactionNo = params.get("vnp_TransactionNo");

        // Try to process payment immediately (useful for localhost where IPN may not reach)
        if ("00".equals(responseCode)) {
            try {
                service.markSucceeded(txnRef, transactionNo);
                logger.info("✅ Payment succeeded for txnRef={} via returnUrl", txnRef);
            } catch (Exception ex) {
                logger.error("❌ Failed to mark payment succeeded for txnRef={}. Will rely on IPN to reconcile.", txnRef, ex);
            }
        } else {
            try {
                service.markFailed(txnRef, "VNPay=" + responseCode);
                logger.info("❌ Payment failed for txnRef={} with responseCode={}", txnRef, responseCode);
            } catch (Exception ex) {
                logger.warn("Failed to mark payment failed for txnRef={}", txnRef, ex);
            }
        }

        // Rebuild query string to pass back to frontend
        StringBuilder queryString = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (!first) queryString.append('&');
            queryString.append(entry.getKey()).append('=').append(URLEncoder.encode(entry.getValue() == null ? "" : entry.getValue(), StandardCharsets.UTF_8));
            first = false;
        }

        String frontendUrl = "http://localhost:5173/#payment-result?" + queryString.toString();
        logger.info("Redirecting to frontend: {}", frontendUrl);
        response.sendRedirect(frontendUrl);
    }

    @PostMapping("/admin/simulate-return")
    public void simulateReturn(@RequestParam String txnRef,
                               @RequestParam(defaultValue = "00") String responseCode,
                               @RequestParam(required = false) String transactionNo,
                               @RequestParam(required = false) Long amount,
                               HttpServletResponse response) throws IOException {
        logger.info("Simulating payment return for txnRef={}, responseCode={}", txnRef, responseCode);

        if ("00".equals(responseCode)) {
            try {
                service.markSucceeded(txnRef, transactionNo);
            } catch (Exception ex) {
                logger.error("Failed to mark payment succeeded for txnRef={}", txnRef, ex);
            }
        } else {
            try {
                service.markFailed(txnRef, "SIMULATED=" + responseCode);
            } catch (Exception ex) {
                logger.warn("Failed to mark payment failed for txnRef={}", txnRef, ex);
            }
        }

        long amountForVnp = amount != null ? amount * 100 : 0; // VNPay format (amount*100)
        String redirectUrl = String.format(
            "http://localhost:5173/#payment-result?vnp_ResponseCode=%s&vnp_TxnRef=%s&vnp_TransactionNo=%s&vnp_Amount=%d",
            URLEncoder.encode(responseCode, StandardCharsets.UTF_8),
            URLEncoder.encode(txnRef == null ? "" : txnRef, StandardCharsets.UTF_8),
            URLEncoder.encode(transactionNo == null ? "" : transactionNo, StandardCharsets.UTF_8),
            amountForVnp
        );

        response.sendRedirect(redirectUrl);
    }

    private boolean validateSecureHash(Map<String, String> params) {
        try {
            String receivedHash = params.get("vnp_SecureHash");
            if (receivedHash == null) return false;

            SortedMap<String, String> sorted = new TreeMap<>();
            for (Map.Entry<String, String> e : params.entrySet()) {
                String k = e.getKey();
                if ("vnp_SecureHash".equals(k) || "vnp_SecureHashType".equals(k)) continue;
                sorted.put(k, e.getValue());
            }

            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (Map.Entry<String, String> e : sorted.entrySet()) {
                if (!first) sb.append('&');
                first = false;
                sb.append(e.getKey()).append('=')
                        .append(URLEncoder.encode(e.getValue() == null ? "" : e.getValue(), StandardCharsets.UTF_8));
            }

            String computed = hmacSHA512(vnp.getHashSecret(), sb.toString());
            return computed.equalsIgnoreCase(receivedHash);
        } catch (Exception ex) {
            return false;
        }
    }

    private String hmacSHA512(String key, String data) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA512");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA512"));
        byte[] digest = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) sb.append(String.format("%02x", b & 0xff));
        return sb.toString();
    }
}
