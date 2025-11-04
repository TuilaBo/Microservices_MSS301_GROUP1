package com.khoavdse170395.paymentservice.domain;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "Payment")
public class PaymentEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer orderId;

    @Column(nullable = false, length = 20)
    private String provider; // VNPAY

    @Column(nullable = false, unique = true, length = 64)
    private String txnRef;

    @Column(nullable = false)
    private Long amountVnd;

    @Column(nullable = false, length = 8)
    private String currency; // VND

    @Column(nullable = false, length = 20)
    private String status; // PENDING | SUCCEEDED | FAILED | CANCELED

    @Column(length = 64)
    private String externalTxnId; // vnp_TransactionNo

    @Column(length = 255)
    private String reason;

    private Timestamp createdAt;
    private Timestamp updatedAt;

    @PrePersist
    public void prePersist(){
        Timestamp now = new Timestamp(System.currentTimeMillis());
        this.createdAt = now;
        this.updatedAt = now;
    }
    @PreUpdate
    public void preUpdate(){
        this.updatedAt = new Timestamp(System.currentTimeMillis());
    }

    public Integer getId(){ return id; }
    public void setId(Integer id){ this.id = id; }
    public Integer getOrderId(){ return orderId; }
    public void setOrderId(Integer orderId){ this.orderId = orderId; }
    public String getProvider(){ return provider; }
    public void setProvider(String provider){ this.provider = provider; }
    public String getTxnRef(){ return txnRef; }
    public void setTxnRef(String txnRef){ this.txnRef = txnRef; }
    public Long getAmountVnd(){ return amountVnd; }
    public void setAmountVnd(Long amountVnd){ this.amountVnd = amountVnd; }
    public String getCurrency(){ return currency; }
    public void setCurrency(String currency){ this.currency = currency; }
    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }
    public String getExternalTxnId(){ return externalTxnId; }
    public void setExternalTxnId(String externalTxnId){ this.externalTxnId = externalTxnId; }
    public String getReason(){ return reason; }
    public void setReason(String reason){ this.reason = reason; }
    public Timestamp getCreatedAt(){ return createdAt; }
    public void setCreatedAt(Timestamp createdAt){ this.createdAt = createdAt; }
    public Timestamp getUpdatedAt(){ return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt){ this.updatedAt = updatedAt; }
}
