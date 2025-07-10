package com.payment.paymentIntegration.paymentService;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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

import com.payment.paymentIntegration.dto.PaymentLinkRequestDto;
import com.payment.paymentIntegration.dto.PaymentOrders;
import com.payment.paymentIntegration.dto.UserSubscription;
import com.payment.paymentIntegration.exception.IllegalArgumentExceptio;
import com.payment.paymentIntegration.exception.RefundAlreadyDone;
import com.payment.paymentIntegration.paymentRepo.PaymentRepo;
import com.payment.paymentIntegration.paymentRepo.SubscriptionRepo;
import com.razorpay.Order;
import com.razorpay.Payment;
import com.razorpay.PaymentLink;
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


	@Autowired
	private PaymentRepo paymentRepo;
	

	
	public Order createOrder(Long UserId,BigDecimal amount,Long orderId) throws RazorpayException
	{
		RazorpayClient razorpayClient=new RazorpayClient(apiKey, apiSecret);
		
		JSONObject jsonObject=new JSONObject();
		BigDecimal amountINPaise =amount.multiply(new BigDecimal("100"));
		jsonObject.put("amount", amountINPaise.intValue());
		jsonObject.put("currency","INR");
		String receiptId ="recepit"+UUID.randomUUID().toString().substring(0, 20);
		jsonObject.put("receipt",receiptId);
		
		Order order=razorpayClient.orders.create(jsonObject);
		
		
		PaymentOrders payorders =new PaymentOrders();
		payorders.setUserId(UserId);
		payorders.setOrderId(orderId);
		payorders.setAmount(amount);
		payorders.setRazorpayOrderId(order.get("id"));
		payorders.setPaymentStatus(order.get("status"));
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
		paymentOrders.setPaymentMethod(payment.get("method"));
		
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
			return payment.get("status").toString();
			
		}
		catch(Exception e)
		{
			return payment.get("status").toString();
			
		}
	}

	
//	public String createPaymentLink(PaymentLinkRequestDto dto) throws RazorpayException {
//	    RazorpayClient razorpayClient = new RazorpayClient(apiKey, apiSecret);
//	    BigDecimal amountInPaise = dto.getAmount().multiply(new BigDecimal("100"));
//
//	    JSONObject request = new JSONObject();
//	    request.put("amount", amountInPaise.intValue());
//	    request.put("currency", "INR");
//	    request.put("accept_partial", false);
//	    request.put("description", dto.getDescription());
//	    request.put("reference_id", dto.getReferenceId() != null ? dto.getReferenceId() : "txn_" + UUID.randomUUID().toString().substring(0, 10));
//
//	    JSONObject customer = new JSONObject();
//	    customer.put("name", dto.getCustomerName());
//	    customer.put("contact", dto.getCustomerContact());
//	    customer.put("email", dto.getCustomerEmail());
//	    request.put("customer", customer);
//
//	    request.put("notify", new JSONObject().put("email", true).put("sms", true));
//
//	    PaymentLink link = razorpayClient.paymentLink.create(request);
//
//	    PaymentOrders payment = new PaymentOrders();
//	    payment.setUserId(dto.getUserId());
//	    payment.setOrderId(dto.getOrderId());
//	    payment.setAmount(dto.getAmount());
//	    payment.setPaymentStatus("created");
//	    payment.setRazorpayReferenceId(link.get("id"));
//	    payment.setPaymentLinkUrl(link.get("short_url"));
//	    payment.setCreatedAt(LocalDateTime.now());
//	    payment.setUpdateAt(LocalDateTime.now());
//
//	    paymentRepo.save(payment);
//
//	    return link.get("short_url");
//	}
	

	
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
	                String method = entity.getString("method");
	                long timestamp = entity.getLong("created_at");
	                LocalDateTime updateAt = Instant.ofEpochSecond(timestamp)
	                    .atZone(ZoneId.of("Asia/Kolkata"))
	                    .toLocalDateTime();

	                PaymentOrders paymentOrders = paymentRepo.findByRazorpayPaymentId(paymentId);
	               if(paymentOrders!=null)
	               {
	               paymentOrders.setPaymentStatus(status);
	               paymentOrders.setUpdateAt(updateAt);
	               paymentOrders.setPaymentMethod(method);  
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
	            "created", tenMinutesAgo);

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
			
			if(!paymentRepo.existsByRazorpayPaymentId(paymentId))
			{
				throw new IllegalArgumentExceptio("Payment ID not found");
			}
			
			PaymentOrders refundedProcessedPayment = paymentRepo.findRefundedProcessedPayment(paymentId);
			
			if(refundedProcessedPayment!=null)
			{
				throw new RefundAlreadyDone("Refund Already Completed");
			}
			
			
			Refund refund=razorpayClient.refunds.create(refundRequest);
			
			 return "Refund initiated with ID: " + refund.get("id");
	  }
}
 