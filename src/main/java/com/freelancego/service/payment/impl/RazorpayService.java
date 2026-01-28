package com.freelancego.service.payment.impl;

import com.freelancego.dto.user.RazorpayOrderResponse;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import jakarta.annotation.PostConstruct;
import org.apache.commons.codec.binary.Hex;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private RazorpayClient client;

    @PostConstruct
    public void init() throws Exception {
        this.client = new RazorpayClient(keyId, keySecret);
    }

    public RazorpayOrderResponse createOrder(double amount, int milestoneId) throws Exception {

        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (int) (amount * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "MILESTONE-" + milestoneId);
        orderRequest.put("payment_capture", 1);

//        Order order = client.orders.create(orderRequest);
        com.razorpay.Order order = client.orders.create(orderRequest);

        RazorpayOrderResponse response = new RazorpayOrderResponse();

        response.setOrderId(order.get("id"));
        response.setAmount(order.get("amount"));
        response.setCurrency(order.get("currency"));
        response.setStatus(order.get("status"));

        return response;
    }

    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) throws Exception {

        String payload = orderId + "|" + paymentId;

        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(keySecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");

        mac.init(secretKey);

        String generatedSignature = Hex.encodeHexString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));

        return generatedSignature.equals(signature);
    }

    public void transfer(String paymentId, JSONObject request) throws Exception {
        client.payments.transfer(paymentId, request);
    }

    public void refundPayment(String paymentId, int amount) throws Exception {

        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", amount);

        client.payments.refund(paymentId, refundRequest);
    }

}
