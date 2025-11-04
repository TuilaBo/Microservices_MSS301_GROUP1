package com.khoavdse170395.paymentservice.controller.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class CreatePaymentRequest {
    @NotNull
    private Integer orderId;

    @Min(1000)
    private Long amountOverrideVnd; // optional: allow override; if null use order.amountVnd

    public Integer getOrderId(){ return orderId; }
    public void setOrderId(Integer orderId){ this.orderId = orderId; }
    public Long getAmountOverrideVnd(){ return amountOverrideVnd; }
    public void setAmountOverrideVnd(Long amountOverrideVnd){ this.amountOverrideVnd = amountOverrideVnd; }
}
