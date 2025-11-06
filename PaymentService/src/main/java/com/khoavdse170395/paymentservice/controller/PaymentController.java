package com.khoavdse170395.paymentservice.controller;


import com.khoavdse170395.paymentservice.controller.dto.CreatePaymentRequest;
import com.khoavdse170395.paymentservice.controller.dto.CreatePaymentResponse;
import com.khoavdse170395.paymentservice.config.PaymentProps;
import com.khoavdse170395.paymentservice.service.PaymentServiceImpl;
import com.khoavdse170395.paymentservice.util.PaymentSigner;
import com.khoavdse170395.paymentservice.controller.dto.MembershipResponse;
import com.khoavdse170395.paymentservice.service.MembershipService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.NumberFormat;
import java.util.*;

@Controller
@RequestMapping("/api/payments/vnpay")
public class PaymentController {
    private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);

    private final PaymentServiceImpl service;
    private final PaymentProps vnp;
    private final MembershipService membershipService;

    public PaymentController(PaymentServiceImpl service, PaymentProps vnp, MembershipService membershipService) {
        this.service = service;
        this.vnp = vnp;
        this.membershipService = membershipService;
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
    @GetMapping("/pay-result")
    public String vnpayReturn(@RequestParam Map<String, String> params, Model model) {
        // Validate secure hash
        if (!validateSecureHash(params)) {
            model.addAttribute("success", false);
            model.addAttribute("message", "Chữ ký không hợp lệ. Giao dịch có thể bị giả mạo!");
            model.addAttribute("txnRef", "-");
            model.addAttribute("responseCode", "97");
            return "payment-result";
        }

        String responseCode = params.get("vnp_ResponseCode");
        String txnRef = params.get("vnp_TxnRef");
        String transactionNo = params.get("vnp_TransactionNo");
        String bankCode = params.get("vnp_BankCode");
        String amountStr = params.get("vnp_Amount");

        // Parse amount (VNPay trả về amount * 100)
        Long amount = null;
        try {
            if (amountStr != null) {
                amount = Long.parseLong(amountStr) / 100;
            }
        } catch (NumberFormatException e) {
            // ignore
        }

        boolean success = "00".equals(responseCode);

        model.addAttribute("success", success);
        model.addAttribute("txnRef", txnRef != null ? txnRef : "-");
        model.addAttribute("responseCode", responseCode != null ? responseCode : "-");
        model.addAttribute("transactionNo", transactionNo);
        model.addAttribute("bankCode", bankCode);

        if (amount != null) {
            NumberFormat formatter = NumberFormat.getInstance(new Locale("vi", "VN"));
            model.addAttribute("amount", formatter.format(amount) + " VND");
        }

        if (success) {
            model.addAttribute("message", "Giao dịch của bạn đã được xử lý thành công!");
            // Try to update DB via service. This may also be done by the IPN endpoint.
            try {
                if (txnRef != null) {
                    service.markSucceeded(txnRef, transactionNo);
                }
                // If there is a membership associated with this payment, include it in the model
                if (txnRef != null) {
                    try {
                        MembershipResponse mem = membershipService.getByPaymentReference(txnRef);
                        model.addAttribute("membership", mem);
                    } catch (Exception ignore) {
                        // no membership found or error; ignore so user still sees payment result
                    }
                }
            } catch (Exception ex) {
                // Log the exception so it can be investigated; do not break the user flow
                logger.error("Failed to mark payment succeeded for txnRef={}. Will rely on IPN to reconcile.", txnRef, ex);
            }
        } else {
            model.addAttribute("message", getErrorMessage(responseCode));
            try {
                if (txnRef != null) {
                    service.markFailed(txnRef, "VNPay=" + responseCode);
                }
            } catch (Exception ex) {
                logger.warn("Failed to mark payment failed for txnRef={}. Will rely on IPN to reconcile.", txnRef, ex);
            }

            // Also try to include membership if present (may show pending state)
            if (txnRef != null) {
                try {
                    MembershipResponse mem = membershipService.getByPaymentReference(txnRef);
                    model.addAttribute("membership", mem);
                } catch (Exception ignore) {
                    // ignore
                }
            }
        }

        return "payment-result";
    }

    @PostMapping("/admin/simulate-return")
    public String simulateReturn(@RequestParam String txnRef,
                                 @RequestParam(defaultValue = "00") String responseCode,
                                 @RequestParam(required = false) String transactionNo,
                                 @RequestParam(required = false) Long amount,
                                 Model model) {
        boolean success = "00".equals(responseCode);

        model.addAttribute("success", success);
        model.addAttribute("txnRef", txnRef != null ? txnRef : "-");
        model.addAttribute("responseCode", responseCode != null ? responseCode : "-");
        model.addAttribute("transactionNo", transactionNo);

        if (amount != null) {
            java.text.NumberFormat formatter = java.text.NumberFormat.getInstance(new java.util.Locale("vi", "VN"));
            model.addAttribute("amount", formatter.format(amount) + " VND");
        }

        if (success) {
            model.addAttribute("message", "Giao dịch của bạn đã được xử lý thành công!");
            try {
                service.markSucceeded(txnRef, transactionNo);
            } catch (Exception ex) {
                logger.error("Failed to mark payment succeeded for txnRef={}.", txnRef, ex);
            }
            // Include membership if any
            if (txnRef != null) {
                try {
                    MembershipResponse mem = membershipService.getByPaymentReference(txnRef);
                    model.addAttribute("membership", mem);
                } catch (Exception ignore) {}
            }
        } else {
            model.addAttribute("message", getErrorMessage(responseCode));
            try {
                service.markFailed(txnRef, "SIMULATED=" + responseCode);
            } catch (Exception ex) {
                logger.warn("Failed to mark payment failed for txnRef={}.", txnRef, ex);
            }
            if (txnRef != null) {
                try {
                    MembershipResponse mem = membershipService.getByPaymentReference(txnRef);
                    model.addAttribute("membership", mem);
                } catch (Exception ignore) {}
            }
        }

        return "payment-result";
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

    private String getErrorMessage(String responseCode) {
        if (responseCode == null) return "Lỗi không xác định";

        return switch (responseCode) {
            case "00" -> "Giao dịch thành công";
            case "07" -> "Trừ tiền thành công. Giao dịch bị nghi ngờ (liên quan tới lừa đảo, giao dịch bất thường).";
            case "09" -> "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng chưa đăng ký dịch vụ InternetBanking tại ngân hàng.";
            case "10" -> "Giao dịch không thành công do: Khách hàng xác thực thông tin thẻ/tài khoản không đúng quá 3 lần";
            case "11" -> "Giao dịch không thành công do: Đã hết hạn chờ thanh toán. Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "12" -> "Giao dịch không thành công do: Thẻ/Tài khoản của khách hàng bị khóa.";
            case "13" -> "Giao dịch không thành công do Quý khách nhập sai mật khẩu xác thực giao dịch (OTP). Xin quý khách vui lòng thực hiện lại giao dịch.";
            case "24" -> "Giao dịch không thành công do: Khách hàng hủy giao dịch";
            case "51" -> "Giao dịch không thành công do: Tài khoản của qu�� khách không đủ số dư để thực hiện giao dịch.";
            case "65" -> "Giao dịch không thành công do: Tài khoản của Quý khách đã vượt quá hạn mức giao dịch trong ngày.";
            case "75" -> "Ngân hàng thanh toán đang bảo trì.";
            case "79" -> "Giao dịch không thành công do: KH nhập sai mật khẩu thanh toán quá số lần quy định. Xin quý khách vui lòng thực hiện lại giao dịch";
            case "99" -> "Các lỗi khác (lỗi còn lại, không có trong danh sách mã lỗi đã liệt kê)";
            default -> "Giao dịch thất bại với mã lỗi: " + responseCode;
        };
    }
}
