package com.khoavdse170395.paymentservice.domain;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "OutboxEvent")
public class OutboxEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, length = 64)
    private String type; // PAYMENT_SUCCEEDED | PAYMENT_FAILED

    @Column(nullable = false, length = 64)
    private String aggregateType; // Payment | Order

    @Column(nullable = false)
    private Integer aggregateId;

    @Lob
    @Column(nullable = false)
    private String payload; // JSON

    private Timestamp createdAt;

    @Column(nullable = false)
    private boolean published = false;

    @PrePersist
    public void prePersist(){
        this.createdAt = new Timestamp(System.currentTimeMillis());
    }

    // getters/setters
    public Integer getId(){ return id; }
    public void setId(Integer id){ this.id = id; }
    public String getType(){ return type; }
    public void setType(String type){ this.type = type; }
    public String getAggregateType(){ return aggregateType; }
    public void setAggregateType(String aggregateType){ this.aggregateType = aggregateType; }
    public Integer getAggregateId(){ return aggregateId; }
    public void setAggregateId(Integer aggregateId){ this.aggregateId = aggregateId; }
    public String getPayload(){ return payload; }
    public void setPayload(String payload){ this.payload = payload; }
    public Timestamp getCreatedAt(){ return createdAt; }
    public void setCreatedAt(Timestamp createdAt){ this.createdAt = createdAt; }
    public boolean isPublished(){ return published; }
    public void setPublished(boolean published){ this.published = published; }
}
