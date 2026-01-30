package com.freelancego.service.payment.impl;

import com.freelancego.dto.user.RazorpayOrderResponse;
import com.razorpay.*;
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

    /* -------------------------------------------------
       CREATE CONTACT (SDK SAFE)
       ------------------------------------------------- */
    public String createContact(String name, String email, String phone) throws RazorpayException {

        JSONObject request = new JSONObject();
        request.put("name", name);
        request.put("email", email);
        request.put("contact", phone);
        request.put("type", "customer");

        Customer customer = client.customers.create(request);
        return customer.get("id");
    }

    /* -------------------------------------------------
       CREATE FUND ACCOUNT (SDK SAFE)
       ------------------------------------------------- */
    public String createFundAccount(
            String contactId,
            String holderName,
            String accountNumber,
            String ifsc
    ) throws RazorpayException {

        JSONObject bankAccount = new JSONObject();
        bankAccount.put("name", holderName);
        bankAccount.put("account_number", accountNumber);
        bankAccount.put("ifsc", ifsc);

        JSONObject request = new JSONObject();
        request.put("contact_id", contactId);
        request.put("account_type", "bank_account");
        request.put("bank_account", bankAccount);

        FundAccount fundAccount = client.fundAccount.create(request);
        return fundAccount.get("id");
    }

    /* -------------------------------------------------
       CREATE ORDER
       ------------------------------------------------- */
    public RazorpayOrderResponse createOrder(double amount, int milestoneId) throws RazorpayException {

        JSONObject request = new JSONObject();
        request.put("amount", Math.round(amount * 100)); // paise
        request.put("currency", "INR");
        request.put("receipt", "MILESTONE-" + milestoneId);
        request.put("payment_capture", 1);

        Order order = client.orders.create(request);

        RazorpayOrderResponse response = new RazorpayOrderResponse();
        response.setOrderId(order.get("id"));
        response.setAmount(Integer.parseInt(order.get("amount").toString()));
        response.setCurrency(order.get("currency"));
        response.setStatus(order.get("status"));

        return response;
    }

    /* -------------------------------------------------
       VERIFY CLIENT PAYMENT SIGNATURE (OPTIONAL)
       ------------------------------------------------- */
    public boolean verifyPaymentSignature(
            String orderId,
            String paymentId,
            String signature
    ) {
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

    /* -------------------------------------------------
       TRANSFER PAYMENT (ESCROW → FREELANCER)
       ------------------------------------------------- */
    public void transfer(String paymentId, JSONObject transferRequest) throws RazorpayException {
        client.payments.transfer(paymentId, transferRequest);
    }

    /* -------------------------------------------------
       REFUND PAYMENT
       ------------------------------------------------- */
    public void refundPayment(String paymentId, double amount) throws RazorpayException {

        JSONObject refundRequest = new JSONObject();
        refundRequest.put("amount", Math.round(amount * 100)); // paise

        client.payments.refund(paymentId, refundRequest);
    }
}
