package com.payment.paymentIntegration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.payment.paymentIntegration.dto.PaymentHistoryDTO;
import com.payment.paymentIntegration.dto.SellerEarningsDTO;
import com.payment.paymentIntegration.paymentService.EarningsService;

@RestController
@RequestMapping("api/earnings")
public class EarningsController {
	
	@Autowired
	EarningsService earningsService;
	
	@GetMapping("/{UserId}")
	public SellerEarningsDTO getEarnings(@PathVariable Long UserId)
	{
		return earningsService.getEarning(UserId);
	}
	
	@GetMapping("/paymentHistory/{UserId}")
	public List<PaymentHistoryDTO> getPaymentHistory(@PathVariable Long UserId)
	{
		
		return  earningsService.getPaymentHistory(UserId);
		
	}
	
	

}
