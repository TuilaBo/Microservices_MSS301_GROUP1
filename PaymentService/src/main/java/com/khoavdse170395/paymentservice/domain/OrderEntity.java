package com.khoavdse170395.paymentservice.domain;

import jakarta.persistence.*;

import java.sql.Timestamp;

@Entity
@Table(name = "[Order]") // SQL Server reserved keyword, use brackets
public class OrderEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false)
    private Integer userId;

    @Column(nullable = false, length = 50)
    private String itemType; // CLASS | QUIZ | SUBSCRIPTION

    @Column(nullable = false)
    private Integer itemId;

    @Column(length = 255)
    private String title;

    @Column(nullable = false)
    private Long amountVnd;

    @Column(nullable = false, length = 20)
    private String status; // CREATED | PENDING | PAID | FAILED | CANCELED

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

    // getters and setters
    public Integer getId(){ return id; }
    public void setId(Integer id){ this.id = id; }
    public Integer getUserId(){ return userId; }
    public void setUserId(Integer userId){ this.userId = userId; }
    public String getItemType(){ return itemType; }
    public void setItemType(String itemType){ this.itemType = itemType; }
    public Integer getItemId(){ return itemId; }
    public void setItemId(Integer itemId){ this.itemId = itemId; }
    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }
    public Long getAmountVnd(){ return amountVnd; }
    public void setAmountVnd(Long amountVnd){ this.amountVnd = amountVnd; }
    public String getStatus(){ return status; }
    public void setStatus(String status){ this.status = status; }
    public Timestamp getCreatedAt(){ return createdAt; }
    public void setCreatedAt(Timestamp createdAt){ this.createdAt = createdAt; }
    public Timestamp getUpdatedAt(){ return updatedAt; }
    public void setUpdatedAt(Timestamp updatedAt){ this.updatedAt = updatedAt; }
}
