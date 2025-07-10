package com.payment.paymentIntegration.paymentRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payment.paymentIntegration.dto.PaymentOrders;
import java.time.LocalDateTime;

import java.util.List;


public interface PaymentRepo extends JpaRepository<PaymentOrders, Long> {
	
	PaymentOrders findByRazorpayOrderId(String razorpayOrderId);
	
	PaymentOrders findByRazorpayPaymentId(String razorpayPaymentId);
	
	boolean existsByRazorpayPaymentId(String razorpayPaymentId);
	
	@Query(value = "SELECT * FROM payment_orders WHERE payment_status = :paymentStatus AND created_at < :time", nativeQuery = true)
	List<PaymentOrders> findByPaymentStatusAndCreatedAtBefore(
	    @Param("paymentStatus") String paymentStatus,
	    @Param("time") LocalDateTime time);
	
	@Query("SELECT p FROM PaymentOrders p WHERE p.razorpayPaymentId = :paymentId AND p.paymentStatus = 'Refunded - processed'")
	PaymentOrders findRefundedProcessedPayment(@Param("paymentId") String paymentId);



	
}
