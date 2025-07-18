package com.payment.paymentIntegration.paymentService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.payment.paymentIntegration.dto.PaymentHistoryDTO;
import com.payment.paymentIntegration.dto.SellerEarningsDTO;
import com.payment.paymentIntegration.entity.PaymentOrders;
import com.payment.paymentIntegration.paymentRepo.PaymentOrdersRepo;

@Service
public class EarningsService {
	
	@Autowired
	PaymentOrdersRepo paymentRepo;
	
	public SellerEarningsDTO getEarning(Long UserId)
	{
		
		BigDecimal totalSalesBySellerId = paymentRepo.findTotalSalesBySellerId(UserId);	
		BigDecimal pendingBalanceBySellerId = paymentRepo.findPendingBalanceBySellerId(UserId);
		BigDecimal withdrawableBalanceBySellerId = paymentRepo.findWithdrawableBalanceBySellerId(UserId);
		
		return new SellerEarningsDTO(totalSalesBySellerId,pendingBalanceBySellerId,withdrawableBalanceBySellerId);
	}
	
	
	public List<PaymentHistoryDTO> getPaymentHistory(Long UserId)
	{
		List<PaymentOrders> bySellerId = paymentRepo.findBySellerIdAndPaymentStatusNotCreated(UserId);
		List<PaymentHistoryDTO> dto=new ArrayList<>();
		
		for(int i=0;i<bySellerId.size();i++)
		{
			PaymentHistoryDTO paymentHistoryDTO=new PaymentHistoryDTO();
			paymentHistoryDTO.setAmount(bySellerId.get(i).getAmount());
			paymentHistoryDTO.setPaymentStatus(bySellerId.get(i).getPaymentStatus());
			paymentHistoryDTO.setCreatedAt(bySellerId.get(i).getCreatedAt());
			paymentHistoryDTO.setOrderId(bySellerId.get(i).getOrderId());
			paymentHistoryDTO.setPaymentMethod(bySellerId.get(i).getPaymentMethod());
			dto.add(paymentHistoryDTO);
		}
		return dto;
	}

}
