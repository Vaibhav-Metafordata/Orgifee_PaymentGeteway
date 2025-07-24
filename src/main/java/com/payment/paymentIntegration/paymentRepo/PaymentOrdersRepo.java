package com.payment.paymentIntegration.paymentRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.payment.paymentIntegration.entity.PaymentOrders;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import java.util.List;


public interface PaymentOrdersRepo extends JpaRepository<PaymentOrders, Long> {
	
	PaymentOrders findByRazorpayOrderId(String razorpayOrderId);
	
	PaymentOrders findByRazorpayPaymentId(String razorpayPaymentId);
	
	boolean existsByRazorpayPaymentId(String razorpayPaymentId);
	
	@Query(value = "SELECT * FROM payment_orders WHERE payment_status = :paymentStatus AND created_at < :time", nativeQuery = true)
	List<PaymentOrders> findByPaymentStatusAndCreatedAtBefore(
	    @Param("paymentStatus") String paymentStatus,
	    @Param("time") LocalDateTime time);
	
	@Query("SELECT p FROM PaymentOrders p WHERE p.razorpayPaymentId = :paymentId AND p.paymentStatus = 'Refunded - processed'")
	PaymentOrders findRefundedProcessedPayment(@Param("paymentId") String paymentId);
	
	
	@Query(value = "SELECT COALESCE(SUM(amount), 0) FROM payment_orders WHERE seller_id = :userId AND payment_status = 'captured'", nativeQuery = true)
	BigDecimal findTotalSalesBySellerId(@Param("userId") Long userId);
	
	@Query(value = "SELECT COALESCE(SUM(amount), 0) FROM payment_orders WHERE seller_id = :userId AND wallet_status = 'pending' AND payment_status = 'captured'", nativeQuery = true)
	BigDecimal findPendingBalanceBySellerId(@Param("userId") Long userId);

	@Query(value = "SELECT COALESCE(SUM(amount), 0) FROM payment_orders WHERE seller_id = :userId AND wallet_status = 'processed' AND payment_status = 'captured'", nativeQuery = true)
	BigDecimal findWithdrawableBalanceBySellerId(@Param("userId") Long userId);

	@Query(value = "SELECT * FROM payment_orders WHERE seller_id = :userId AND payment_status <> 'Created'", nativeQuery = true)
	List<PaymentOrders> findBySellerIdAndPaymentStatusNotCreated(@Param("userId") Long userId);

	@Query("SELECT p FROM PaymentOrders p WHERE p.sellerId = :sellerId AND p.createdAt >= :fromDate AND p.paymentStatus <> 'created'")
	List<PaymentOrders> findHistoryBySellerID(
	    @Param("sellerId") Long sellerId,
	    @Param("fromDate") LocalDateTime fromDate
	);

	
	PaymentOrders findByRefundId(String refundId);
	
	List<PaymentOrders> findBySellerId(Long sellerId);
	
}
