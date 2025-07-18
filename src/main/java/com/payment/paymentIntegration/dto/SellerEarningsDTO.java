package com.payment.paymentIntegration.dto;

import java.math.BigDecimal;

public class SellerEarningsDTO {
	
	 private BigDecimal totalSales;
	    private BigDecimal pendingBalance;
	    private BigDecimal withdrawable;
	    
	    
	    
	    
		public SellerEarningsDTO(BigDecimal totalSales, BigDecimal pendingBalance, BigDecimal withdrawable) {
			super();
			this.totalSales = totalSales;
			this.pendingBalance = pendingBalance;
			this.withdrawable = withdrawable;
		}
		public BigDecimal getTotalSales() {
			return totalSales;
		}
		public void setTotalSales(BigDecimal totalSales) {
			this.totalSales = totalSales;
		}
		public BigDecimal getPendingBalance() {
			return pendingBalance;
		}
		public void setPendingBalance(BigDecimal pendingBalance) {
			this.pendingBalance = pendingBalance;
		}
		public BigDecimal getWithdrawable() {
			return withdrawable;
		}
		public void setWithdrawable(BigDecimal withdrawable) {
			this.withdrawable = withdrawable;
		}

}
