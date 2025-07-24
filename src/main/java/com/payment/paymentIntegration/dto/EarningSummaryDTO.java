package com.payment.paymentIntegration.dto;

import java.math.BigDecimal;
import java.util.List;


public class EarningSummaryDTO {
	
	 private BigDecimal totalEarnings;
	    private BigDecimal sevenDaysEarnings;
	    private List<EarningHistoryDTO> earningHistory;
	    
		public EarningSummaryDTO(BigDecimal totalEarnings, BigDecimal sevenDaysEarnings,
				List<EarningHistoryDTO> earningHistory) {
			super();
			this.totalEarnings = totalEarnings;
			this.sevenDaysEarnings = sevenDaysEarnings;
			this.earningHistory = earningHistory;
		}

		public BigDecimal getTotalEarnings() {
			return totalEarnings;
		}

		public void setTotalEarnings(BigDecimal totalEarnings) {
			this.totalEarnings = totalEarnings;
		}

		public BigDecimal getSevenDaysEarnings() {
			return sevenDaysEarnings;
		}

		public void setSevenDaysEarnings(BigDecimal sevenDaysEarnings) {
			this.sevenDaysEarnings = sevenDaysEarnings;
		}

		public List<EarningHistoryDTO> getEarningHistory() {
			return earningHistory;
		}

		public void setEarningHistory(List<EarningHistoryDTO> earningHistory) {
			this.earningHistory = earningHistory;
		}

		public EarningSummaryDTO() {
			super();
			// TODO Auto-generated constructor stub
		}
		
	    
	    

}
