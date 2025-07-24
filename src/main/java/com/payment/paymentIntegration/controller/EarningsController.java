package com.payment.paymentIntegration.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.paymentIntegration.dto.EarningSummaryDTO;
import com.payment.paymentIntegration.dto.PaymentHistoryDTO;
import com.payment.paymentIntegration.dto.SellerEarningsDTO;
import com.payment.paymentIntegration.paymentService.DemoEarningsService;
import com.payment.paymentIntegration.paymentService.EarningService;

@RestController
@RequestMapping("api/earnings")
public class EarningsController {
	
	@Autowired
	DemoEarningsService earningsService;
	
	@Autowired
	EarningService earningService;
	
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
	
	@GetMapping("/history")
    public ResponseEntity<EarningSummaryDTO> getEarningHistory(
            @RequestParam Long sellerId,
            @RequestParam(required = false) String filter,
            @RequestParam(required = false) String sort) {

        EarningSummaryDTO summary = earningService.getEarningHistory(sellerId, filter, sort);
        return ResponseEntity.ok(summary);
    }
	
	

}
