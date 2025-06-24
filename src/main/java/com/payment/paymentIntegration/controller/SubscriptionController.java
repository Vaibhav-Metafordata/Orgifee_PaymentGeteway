package com.payment.paymentIntegration.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.paymentIntegration.paymentService.SubscriptionService;

@RestController
@RequestMapping("api/subscription")
public class SubscriptionController {
	
	@Autowired
	SubscriptionService subscriptionService;
	
	 @PostMapping("/create-subscription")
	    public ResponseEntity<String> createSubscription(@RequestParam Long userId) {
	        return subscriptionService.createSubscription(userId);
	    }
	 
	 
	 @PostMapping("/webhook")
	    public ResponseEntity<String> handleWebhook(
	            @RequestBody String payload,
	            @RequestHeader("X-Razorpay-Signature") String signature) {
		 
		 	 return subscriptionService.processWebhook(payload, signature);

	    }

}
