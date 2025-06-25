package com.payment.paymentIntegration.exception;

public class RefundAlreadyDone extends RuntimeException {
	
	public RefundAlreadyDone(String msg)
	{
		super(msg);
	}

}
