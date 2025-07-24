package com.payment.paymentIntegration.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class EarningHistoryDTO {

    private Long orderId;         
    private String productName;     
    private String quantity;        
    private BigDecimal amount;      
    private LocalDate date;         
    private String paymentMethod;
    
	public EarningHistoryDTO(Long orderId, String productName, String quantity, BigDecimal amount, LocalDate date,
			String paymentMethod) {
		super();
		this.orderId = orderId;
		this.productName = productName;
		this.quantity = quantity;
		this.amount = amount;
		this.date = date;
		this.paymentMethod = paymentMethod;
	}

	public Long getOrderId() {
		return orderId;
	}

	public void setOrderId(Long orderId) {
		this.orderId = orderId;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public String getQuantity() {
		return quantity;
	}

	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getPaymentMethod() {
		return paymentMethod;
	}

	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}

	public EarningHistoryDTO() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	
    
    
    
    
    
	
    
}