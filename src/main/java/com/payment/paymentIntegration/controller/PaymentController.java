 package com.payment.paymentIntegration.controller;


import java.math.BigDecimal;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.paymentIntegration.dto.PaymentLinkRequestDto;
import com.payment.paymentIntegration.dto.PaymentOrders;
import com.payment.paymentIntegration.paymentRepo.PaymentRepo;
import com.payment.paymentIntegration.paymentService.PaymentService;
import com.razorpay.Order;
import com.razorpay.RazorpayException;

@RestController
@RequestMapping("api/payments")
public class PaymentController {
	
	@Value("${razorpay.api.secret}")
	private String apiSecret;
	
	@Autowired
	 private PaymentService paymentService;
	
	
	@GetMapping("/config")
	public ResponseEntity<Map<String, String>> getRazorpayKey() {
	    return ResponseEntity.ok(Map.of("key", paymentService.getApiKey()));
	}
	
	@PostMapping("/pay")
	public ResponseEntity<String> createOrder(@RequestParam Long userId ,@RequestParam BigDecimal amount,@RequestParam Long orderId) throws RazorpayException
	{	
		Order order=paymentService.createOrder(userId,amount,orderId);
		return ResponseEntity.status(HttpStatus.CREATED).body(order.toJson().toString());
		
	}
	
	
	@PostMapping("/verify")
	public ResponseEntity<String> verifyPayment(@RequestBody Map<String, Object> data) throws RazorpayException
	{
		
		return ResponseEntity.status(HttpStatus.CREATED).body(paymentService.verifyPayment(data));
		
	}
	
	
	 @PostMapping("/webhook")
	    public ResponseEntity<String> handleWebhook(
	            @RequestBody String payload,
	            @RequestHeader("X-Razorpay-Signature") String signature) {
		 
		 	 return paymentService.processWebhook(payload, signature);
  
	    }
	 
	 @PutMapping("/refund")
	 public ResponseEntity<String> refundProsess(@RequestParam String paymentId) throws RazorpayException
	 {
		 return ResponseEntity.status(HttpStatus.ACCEPTED).body(paymentService.refundPayment(paymentId));
	 }

}
