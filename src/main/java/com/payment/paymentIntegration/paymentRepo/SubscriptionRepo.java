package com.payment.paymentIntegration.paymentRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.payment.paymentIntegration.entity.UserSubscription;

import java.util.List;

@Repository
public interface SubscriptionRepo extends JpaRepository<UserSubscription, Long> {
	
	UserSubscription findByRazorpaySubscriptionId(String razorpaySubscriptionId);
	
	@Query("SELECT s FROM UserSubscription s WHERE s.userId = :userId And s.status = 'active'")
	UserSubscription findActiveSubscriptionByUserId(@Param("userId") Long userId);


}
