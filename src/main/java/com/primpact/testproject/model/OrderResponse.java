package com.primpact.testproject.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

// CHANGE SCENARIO: API BREAKING
public class OrderResponse {

    private Long id;
    private BigDecimal amount;
    private String status;
    private LocalDateTime createdAt;
    private boolean discountApplied;

    public OrderResponse() {
    }

    public OrderResponse(Long id, BigDecimal amount, String status, LocalDateTime createdAt) {
        this.id = id;
        this.amount = amount;
        this.status = status;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isDiscountApplied() {
        return discountApplied;
    }

    public void setDiscountApplied(boolean discountApplied) {
        this.discountApplied = discountApplied;
    }
}
