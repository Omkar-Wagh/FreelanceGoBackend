package com.freelancego.service.payment.impl;

import com.freelancego.dto.user.RazorpayOrderResponse;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Order;
import com.razorpay.Utils;
import jakarta.annotation.PostConstruct;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RazorpayService {

    @Value("${razorpay.key.id}")
    private String keyId;

    @Value("${razorpay.key.secret}")
    private String keySecret;

    private RazorpayClient client;

    @PostConstruct
    public void init() throws RazorpayException {
        this.client = new RazorpayClient(keyId, keySecret);
    }

    /** Create Razorpay Contact */
    public String createContact(String name, String email, String phone) throws RazorpayException {
        JSONObject req = new JSONObject();
        req.put("name", name);
        req.put("email", email);
        req.put("contact", phone);
        req.put("type", "customer");

        // Use the standard internal method that returns the specific SDK Response type
        // Then convert to JSON.
        return client.post("contacts", req).toJson().getString("id");
    }

    /** Create Fund Account */
    public String createFundAccount(String contactId, String holder, String account, String ifsc) throws RazorpayException {
        JSONObject bank = new JSONObject();
        bank.put("name", holder);
        bank.put("account_number", account);
        bank.put("ifsc", ifsc);

        JSONObject req = new JSONObject();
        req.put("contact_id", contactId);
        req.put("account_type", "bank_account");
        req.put("bank_account", bank);

        return client.post("fund_accounts", req).toJson().getString("id");
    }

    /** Create Razorpay Order */
    public RazorpayOrderResponse createOrder(double amount, int milestoneId) throws RazorpayException {
        JSONObject orderRequest = new JSONObject();
        orderRequest.put("amount", (long) Math.round(amount * 100));
        orderRequest.put("currency", "INR");
        orderRequest.put("receipt", "MILESTONE-" + milestoneId);
        orderRequest.put("payment_capture", 1);

        Order order = client.orders.create(orderRequest);

        RazorpayOrderResponse response = new RazorpayOrderResponse();
        // Use .get() method which is the most compatible way to extract data from SDK objects
        response.setOrderId(order.get("id").toString());
        response.setAmount(Integer.parseInt(order.get("amount").toString()));
        response.setCurrency(order.get("currency").toString());
        response.setStatus(order.get("status").toString());

        return response;
    }

    /** Verify Payment Signature */
    public boolean verifyPaymentSignature(String orderId, String paymentId, String signature) {
        try {
            JSONObject options = new JSONObject();
            options.put("razorpay_order_id", orderId);
            options.put("razorpay_payment_id", paymentId);
            options.put("razorpay_signature", signature);

            return Utils.verifyPaymentSignature(options, keySecret);
        } catch (RazorpayException e) {
            return false;
        }
    }

    public void transfer(String paymentId, JSONObject request)
            throws Exception {
        client.payments.transfer(paymentId, request);
    }

    /** Refund payment */
    public void refundPayment(String paymentId, double amount) throws RazorpayException {
        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", (long) Math.round(amount * 100));
        client.payments.refund(paymentId, refundRequest);
    }

}
