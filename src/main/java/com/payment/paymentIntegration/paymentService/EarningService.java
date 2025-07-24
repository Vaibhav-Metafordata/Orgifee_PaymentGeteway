package com.payment.paymentIntegration.paymentService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.payment.paymentIntegration.dto.EarningHistoryDTO;
import com.payment.paymentIntegration.dto.EarningSummaryDTO;
import com.payment.paymentIntegration.entity.PaymentOrders;
import com.payment.paymentIntegration.paymentRepo.PaymentOrdersRepo;

@Service
public class EarningService {

		@Autowired
	    private PaymentOrdersRepo paymentOrdersRepository;

	    @Autowired
	    private RestTemplate restTemplate;

    private static final String BASE_ORDER_API_URL = "http://13.234.177.243/product-service/order/";
    private static final String AUTH_TOKEN = "eyJhbGciOiJIUzM4NCJ9.eyJyb2xlIjoiYWRtaW4iLCJjb21wYW55bmFtZSI6IlpZVkVSIiwibG9jYXRpb24iOiJhc2lhIHBhY2lmaWMiLCJzdWIiOiJzYWFjaGluenl2ZXIiLCJpYXQiOjE3NTMzMzIxNTgsImV4cCI6MTc1MzQ0MDE1OH0.VyNcVR1piHqpt9256mtUKAQ0AAepfVnMcUIyG73hGjs0XAHDqZRV5I4aQ8NZf3QT";

 

    public EarningSummaryDTO getEarningHistory(Long sellerId, String filter, String sort) {

        LocalDate fromLocalDate = switch (filter != null ? filter.toLowerCase() : "") {
            case "month" -> LocalDate.now().minusMonths(1);
            case "year" -> LocalDate.now().minusYears(1);
            default -> LocalDate.now().minusDays(7); 
        };
        LocalDateTime fromDate = fromLocalDate.atStartOfDay();

        List<PaymentOrders> allOrders = paymentOrdersRepository.findBySellerId(sellerId);

        List<PaymentOrders> filteredOrders = paymentOrdersRepository.findHistoryBySellerID(sellerId, fromDate);

        if (sort != null && !sort.isBlank()) {
            String sortLower = sort.toLowerCase();
            List<String> validMethods = List.of("netbanking", "wallet", "upi", "card");

            if (validMethods.contains(sortLower)) {
                filteredOrders = filteredOrders.stream()
                    .filter(o -> sortLower.equalsIgnoreCase(o.getPaymentMethod()))
                    .collect(Collectors.toList());
            } else if ("paymentMethod".equalsIgnoreCase(sort)) {
                filteredOrders.sort(Comparator.comparing(
                    PaymentOrders::getPaymentMethod,
                    Comparator.nullsLast(String::compareToIgnoreCase)
                ));
            }
        }

        BigDecimal totalEarnings = BigDecimal.ZERO;
        BigDecimal filteredEarnings = BigDecimal.ZERO;
        List<EarningHistoryDTO> earningHistory = new ArrayList<>();

        for (PaymentOrders order : allOrders) {
            if (!"created".equalsIgnoreCase(order.getPaymentStatus())) {
                totalEarnings = totalEarnings.add(order.getAmount());
            }
        }

        for (PaymentOrders order : filteredOrders) {
            if (!"created".equalsIgnoreCase(order.getPaymentStatus())) {
                filteredEarnings = filteredEarnings.add(order.getAmount());

                String url = BASE_ORDER_API_URL + order.getOrderId();
                HttpHeaders headers = new HttpHeaders();
                headers.setBearerAuth(AUTH_TOKEN);
                HttpEntity<String> entity = new HttpEntity<>(headers);

                String productName = "N/A";
                String quantity = "N/A";

                try {
                    ResponseEntity<JsonNode> response = restTemplate.exchange(url, HttpMethod.GET, entity, JsonNode.class);
                    JsonNode orderItems = response.getBody().get("success").get("orderItems");

                    if (orderItems != null && orderItems.isArray() && orderItems.size() > 0) {
                        JsonNode item = orderItems.get(0);
                        productName = item.get("name").asText();
                        quantity = item.get("quantity").asText() + item.get("unit").asText();
                    }
                } catch (Exception e) {
                    System.err.println("Error fetching product details for orderId: " + order.getOrderId());
                }

                EarningHistoryDTO dto = new EarningHistoryDTO(
                    order.getOrderId(),
                    productName,
                    quantity,
                    order.getAmount(),
                    order.getCreatedAt().toLocalDate(),
                    order.getPaymentMethod()
                );

                earningHistory.add(dto);
            }
        }

        return new EarningSummaryDTO(
            totalEarnings,
            filteredEarnings,
            earningHistory
        );
    }

}
