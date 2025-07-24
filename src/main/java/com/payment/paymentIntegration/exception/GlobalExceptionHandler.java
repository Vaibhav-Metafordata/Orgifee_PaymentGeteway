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
	
	@ExceptionHandler(IllegalArgumentExceptio.class)
	public ResponseEntity<Map<String,String>> handleIllegalArgumentException(IllegalArgumentExceptio ex)
	{
		Map<String,String> responce=new HashMap<>();
		responce.put("error", "Input Error");
		responce.put("message", ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responce);
	}
	
	@ExceptionHandler(SubscriptionNotFound.class)
	public ResponseEntity<Map<String,String>> handleSubscriptionNotFoundException(SubscriptionNotFound ex)
	{
		Map<String,String> responce=new HashMap<>();
		responce.put("error", "Input Error");
		responce.put("message", ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responce);
	}
	
	@ExceptionHandler(SubscriptionUserMismatchException.class)
	public ResponseEntity<Map<String,String>> handleSubscriptionUserMismatchException(SubscriptionUserMismatchException ex)
	{
		Map<String,String> responce=new HashMap<>();
		responce.put("error", "Input Error");
		responce.put("message", ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responce);
	}
	
	@ExceptionHandler(InvalidPayloadException.class)
	public ResponseEntity<Map<String,String>> handleNullPayloadException(InvalidPayloadException ex)
	{
		Map<String,String> responce=new HashMap<>();
		responce.put("error", "Input Error");
		responce.put("message", ex.getMessage());
		
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responce);
	}

}
