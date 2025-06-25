package com.payment.paymentIntegration.exception;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	@ExceptionHandler(SubscriptionAlreadyExistsException.class)
	public ResponseEntity<Map<String,String>> handleSubscriptionException(SubscriptionAlreadyExistsException ex)
	{
		Map<String,String> responce=new HashMap<>();
		responce.put("error", "Subscription Error");
		responce.put("message", ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responce);
		
	}
	
	@ExceptionHandler(RefundAlreadyDone.class)
	public ResponseEntity<Map<String,String>> handleRefundException(RefundAlreadyDone ex)
	{
		Map<String,String> responce=new HashMap<>();
		responce.put("error", "Refund Error");
		responce.put("message", ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responce);
	}

}
