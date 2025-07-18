package com.payment.paymentIntegration.paymentService;

import java.security.MessageDigest;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.payment.paymentIntegration.entity.UserSubscription;
import com.payment.paymentIntegration.exception.SubscriptionAlreadyExistsException;
import com.payment.paymentIntegration.paymentRepo.SubscriptionRepo;
import com.razorpay.Plan;
import com.razorpay.RazorpayClient;

@Service
public class SubscriptionService {
	
	@Value("${razorpay.api.key}")
	private String apiKey;
	

	@Value("${razorpay.api.secret}")
	private String apiSecret;
	
	@Autowired 
	SubscriptionRepo subscriptionRepo;
	
	
	public ResponseEntity<String> createSubscription(Long userId) {
		
		UserSubscription userSubscription= subscriptionRepo.findActiveSubscriptionByUserId(userId);
		if(userSubscription!=null)
		{
			throw new SubscriptionAlreadyExistsException("User already has an active subscription");
		}
		
        try {
        	
            RazorpayClient razorpay = new RazorpayClient(apiKey, apiSecret);

            JSONObject request = new JSONObject();
            request.put("plan_id","plan_QiZwsTPtdls3gA"); 
            request.put("total_count",12);
            request.put("customer_notify", 1);
           
            JSONObject notes = new JSONObject();
            notes.put("user_id", String.valueOf(userId));
            request.put("notes", notes);

            com.razorpay.Subscription razorpaySub = razorpay.subscriptions.create(request);
            String subscriptionId = razorpaySub.get("id");
            String shortUrl = razorpaySub.get("short_url");
            String customerId = razorpaySub.get("customer_id");
            String planId = razorpaySub.get("plan_id");
            String status = razorpaySub.get("status");
            
            Plan plan=razorpay.plans.fetch(planId);   
            JSONObject item=plan.get("item");
            
            JSONObject itemObj=new JSONObject(item.toString());
            String planName = itemObj.getString("name");
            
            UserSubscription sub = new UserSubscription();
            sub.setRazorpaySubscriptionId(subscriptionId);
            sub.setSubscriptionLink(shortUrl); 
            sub.setCustomerId(customerId);
            sub.setPlanId(planId);
            sub.setStatus(status);
            sub.setUserId(userId);
            sub.setStartDate(null); 
            sub.setEndDate(null);
            sub.setCreateAt(LocalDateTime.now());
            sub.setPlanName(planName);
            subscriptionRepo.save(sub);

            return ResponseEntity.ok(shortUrl);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error: " + e.getMessage());
        }
    }
	
	
	public ResponseEntity<String> processWebhook(String payload, String signature){
		 try {
            if (!verifySignature(payload, signature, apiSecret)) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
            }

            JSONObject json = new JSONObject(payload);
            String event = json.getString("event");
            
         
            	if (event.startsWith("subscription.")) {
                JSONObject entity = json
                        .getJSONObject("payload")
                        .getJSONObject("subscription")
                        .getJSONObject("entity");
               
                Long UserIdFromNotes=Long.parseLong(entity.getJSONObject("notes").getString("user_id"));
                System.out.println(UserIdFromNotes);
                
                
                		
                UserSubscription sub =subscriptionRepo.findByRazorpaySubscriptionId(entity.getString("id"));
                		
                sub.setRazorpaySubscriptionId(entity.getString("id"));
                sub.setCustomerId(entity.getString("customer_id"));
                sub.setPlanId(entity.getString("plan_id"));
                sub.setStatus(entity.getString("status"));
                Long userId = Long.parseLong(entity.getJSONObject("notes").getString("user_id"));
                sub.setUserId(userId);

                long startEpoch = entity.optLong("current_start");
                long endEpoch = entity.optLong("current_end");

                sub.setStartDate(Instant.ofEpochSecond(startEpoch)
                                .atZone(ZoneId.of("Asia/Kolkata"))
                                .toLocalDateTime());
                sub.setEndDate(Instant.ofEpochSecond(endEpoch)
                                .atZone(ZoneId.of("Asia/Kolkata"))
                                .toLocalDateTime());
                sub.setCreateAt(LocalDateTime.now());

                subscriptionRepo.save(sub);
        }       
        return ResponseEntity.ok("Webhook processed");
        

	    } catch (Exception e) {
	        e.printStackTrace();
	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
	    }
		
	}
	private boolean verifySignature(String payload, String actualSignature, String secret) throws Exception {
	    Mac sha256Hmac = Mac.getInstance("HmacSHA256");
	    SecretKeySpec secretKey = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
	    sha256Hmac.init(secretKey);

	    byte[] hash = sha256Hmac.doFinal(payload.getBytes());
	    String generatedSignature = org.apache.commons.codec.binary.Hex.encodeHexString(hash);

	    return MessageDigest.isEqual(generatedSignature.getBytes(), actualSignature.getBytes());
	}
}
