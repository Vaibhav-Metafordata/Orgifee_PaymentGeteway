package com.payment.paymentIntegration.paymentService;

import java.util.Date;
import java.util.List;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.payment.paymentIntegration.dto.PaymentOrders;
import com.payment.paymentIntegration.paymentRepo.PaymentRepo;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Refund;
import com.razorpay.Utils;

@Service
public class PaymentService {
	
	@Value("${razorpay.api.key}")
	private String apiKey;
	

	@Value("${razorpay.api.secret}")
	private String apiSecret;
	
	public String getApiKey() {
		return apiKey;
	}


	public PaymentRepo getPaymentRepo() {
		return paymentRepo;
	}


	@Autowired
	private PaymentRepo paymentRepo;

	
	public Order createOrder(PaymentOrders paymentOrders) throws RazorpayException
	{
		RazorpayClient razorpayClient=new RazorpayClient(apiKey, apiSecret);
		
		JSONObject jsonObject=new JSONObject();
		jsonObject.put("amount", paymentOrders.getAmount()*100);
		jsonObject.put("currency","INR");
		jsonObject.put("receipt",paymentOrders.getEmail());
		
		Order order=razorpayClient.orders.create(jsonObject);
		
		
		PaymentOrders payorders =new PaymentOrders();
		payorders.setName(paymentOrders.getName());
		payorders.setEmail(paymentOrders.getEmail());
		payorders.setAmount(paymentOrders.getAmount());
		payorders.setOrderStatus(order.get("status"));
		payorders.setPh_no(paymentOrders.getPh_no());
		payorders.setRazorpayOrderId(order.get("id"));
		payorders.setRazorpayPaymentId(paymentOrders.getRazorpayPaymentId());
		payorders.setPaymentStatus(paymentOrders.getPaymentStatus());
		payorders.setCreatedAt(LocalDateTime.now());
		payorders.setUpdateAt(LocalDateTime.now());
		
		paymentRepo.save(payorders);

		return order;		
	}
	
	
	public String verifyPayment(Map<String,Object> data) throws RazorpayException
	{
		RazorpayClient razorpayClient=new RazorpayClient(apiKey, apiSecret);
		
		PaymentOrders paymentOrders = paymentRepo.findByRazorpayOrderId(data.get("razorpay_order_id").toString());
		paymentOrders.setRazorpayPaymentId(data.get("razorpay_payment_id").toString());
		
		Payment payment =razorpayClient.payments.fetch(data.get("razorpay_payment_id").toString());
		
		paymentOrders.setPaymentStatus(payment.get("status"));
		
		Date createdAtDate = (Date) payment.get("created_at");

		LocalDateTime createdAt = createdAtDate.toInstant()
		    .atZone(ZoneId.of("Asia/Kolkata"))
		    .toLocalDateTime();


		JSONObject jsonObject =new JSONObject();
		jsonObject.put("razorpay_order_id", data.get("razorpay_order_id"));
		jsonObject.put("razorpay_payment_id", data.get("razorpay_payment_id"));
		jsonObject.put("razorpay_signature", data.get("razorpay_signature"));
		
		try
		{		
			Utils.verifyPaymentSignature(jsonObject, apiSecret);
			paymentOrders.setUpdateAt(createdAt);
			paymentRepo.save(paymentOrders);
			return "Payment verified and recorded!";
			
		}
		catch(Exception e)
		{
			return "Payment verification failed!";
			
		}
	}
	
	public ResponseEntity<String> processWebhook(String payload, String signature){
		 try {
	            if (!verifySignature(payload, signature, apiSecret)) {
	                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid Signature");
	            }

	            JSONObject json = new JSONObject(payload);
	            String event = json.getString("event");
	            
	            if (event.startsWith("payment.")) {
	                JSONObject entity = json
	                    .getJSONObject("payload")
	                    .getJSONObject("payment")
	                    .getJSONObject("entity");

	                String paymentId = entity.getString("id");
	                String status = entity.getString("status");
	                long timestamp = entity.getLong("created_at");
	                LocalDateTime updateAt = Instant.ofEpochSecond(timestamp)
	                    .atZone(ZoneId.of("Asia/Kolkata"))
	                    .toLocalDateTime();


	                PaymentOrders paymentOrders = paymentRepo.findByRazorpayPaymentId(paymentId);
	               if(paymentOrders!=null)
	               {
	               paymentOrders.setPaymentStatus(status);
	               paymentOrders.setUpdateAt(updateAt);
	               paymentRepo.save(paymentOrders);

	               }
	            }
	            else if (event.startsWith("refund.")) {
	                JSONObject entity = json
	                    .getJSONObject("payload")
	                    .getJSONObject("refund")
	                    .getJSONObject("entity");

	                String paymentId = entity.getString("payment_id"); 
	                String status = entity.getString("status");
	                long timestamp = entity.getLong("created_at");
	                LocalDateTime updateAt = Instant.ofEpochSecond(timestamp)
	                    .atZone(ZoneId.of("Asia/Kolkata"))
	                    .toLocalDateTime();


	                PaymentOrders paymentOrders = paymentRepo.findByRazorpayPaymentId(paymentId);
	                if (paymentOrders != null) {
	                    paymentOrders.setPaymentStatus("Refunded - " + status);
	                    paymentOrders.setUpdateAt(updateAt);
	                    paymentOrders.setRefundId(entity.getString("id"));
	                    paymentRepo.save(paymentOrders);
	                }
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
        String generatedSignature = Hex.encodeHexString(hash);
        return generatedSignature.equals(actualSignature);
    }
	
	  @Scheduled(fixedRate = 3600000) 
	    public void deleteStaleOrders() {
	        LocalDateTime tenMinutesAgo = LocalDateTime.now().minusHours(24);
	        List<PaymentOrders> staleOrders = paymentRepo.findByPaymentStatusAndCreatedAtBefore(
	            "pending", tenMinutesAgo);

	        if (!staleOrders.isEmpty()) {
	        	paymentRepo.deleteAll(staleOrders);
	            System.out.println("Deleted " + staleOrders.size() + " stale orders.");
	        }
	    }
	  
	  public String refundPayment(String paymentId) throws RazorpayException
	  {
		 
			RazorpayClient razorpayClient=new RazorpayClient(apiKey,apiSecret);
			JSONObject refundRequest=new JSONObject();
			refundRequest.put("payment_id", paymentId);
			Refund refund=razorpayClient.refunds.create(refundRequest);
			
			 return "Refund initiated with ID: " + refund.get("id");
			
	  }
	
}
 