package com.payment.paymentIntegration.dto;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PaymentHistoryDTO {
    private Long orderId;
    private BigDecimal amount;
    private String paymentStatus;
    private String paymentMethod;
    private LocalDateTime createdAt;

 
    public PaymentHistoryDTO() {}

    public PaymentHistoryDTO(Long orderId, BigDecimal amount, String paymentStatus,String paymentMethod, LocalDateTime createdAt) {
        this.orderId = orderId;
        this.amount = amount;
        this.paymentStatus = paymentStatus;
        this.paymentMethod = paymentMethod;
        this.createdAt = createdAt;
    }

    
    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
