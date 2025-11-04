package com.khoavdse170395.paymentservice.controller.dto;

public class CreatePaymentResponse {
    private Integer paymentId;
    private String txnRef;
    private String redirectUrl;

    public CreatePaymentResponse(Integer paymentId, String txnRef, String redirectUrl){
        this.paymentId = paymentId; this.txnRef = txnRef; this.redirectUrl = redirectUrl;
    }
    public Integer getPaymentId(){ return paymentId; }
    public String getTxnRef(){ return txnRef; }
    public String getRedirectUrl(){ return redirectUrl; }
}
