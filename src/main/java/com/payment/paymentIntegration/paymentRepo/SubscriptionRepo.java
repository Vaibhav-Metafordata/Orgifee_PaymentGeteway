package com.payment.paymentIntegration.paymentRepo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.payment.paymentIntegration.dto.UserSubscription;
import java.util.List;

@Repository
public interface SubscriptionRepo extends JpaRepository<UserSubscription, Long> {
	
	UserSubscription findByRazorpaySubscriptionId(String razorpaySubscriptionId);

}
