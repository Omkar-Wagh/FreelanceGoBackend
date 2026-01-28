package com.freelancego.service.payment.impl;

import com.freelancego.dto.freelancer.PayoutSetupRequest;
import com.freelancego.dto.user.MilestonePaymentResponse;
import com.freelancego.dto.user.RazorpayOrderResponse;
import com.freelancego.enums.ContractStatus;
import com.freelancego.enums.MilestoneStatus;
import com.freelancego.enums.PaymentStatus;
import com.freelancego.enums.PayoutAccountStatus;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.exception.InvalidIdException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.model.*;
import com.freelancego.repo.*;
import com.freelancego.service.Milestone.MilestoneService;
import com.freelancego.service.payment.PaymentService;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.apache.commons.codec.binary.Hex;

import jakarta.transaction.Transactional;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MilestoneRepository milestoneRepository;
    private final RazorpayService razorpayService;
    private final FreelancerRepository freelancerRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;
    private final MilestoneService milestoneService;

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${webhook.secret}")
    private String webhookSecret;

    public PaymentServiceImpl(PaymentRepository paymentRepository, MilestoneRepository milestoneRepository, RazorpayService razorpayService, FreelancerRepository freelancerRepository, UserRepository userRepository, ContractRepository contractRepository, MilestoneService milestoneService) {
        this.paymentRepository = paymentRepository;
        this.milestoneRepository = milestoneRepository;
        this.razorpayService = razorpayService;
        this.freelancerRepository = freelancerRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.milestoneService = milestoneService;
    }

    /** Verify signature */
    private void verifySignature(String payload, String signature) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        SecretKeySpec secretKey = new SecretKeySpec(webhookSecret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
        mac.init(secretKey);
        String generatedSignature = Hex.encodeHexString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));
        if (!generatedSignature.equals(signature)) {
            throw new RuntimeException("Invalid Razorpay webhook signature");
        }
    }

    /** Process client → platform payments */
    public void processPaymentWebhook(String payload, String signature) {
        try{
            verifySignature(payload, signature);
        }catch (Exception e){
            throw new RuntimeException("issue with webhook processing during client to platform payment");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        if (!eventType.startsWith("payment.")) return;

        JSONObject paymentEntity = event.getJSONObject("payload")
                .getJSONObject("payment")
                .getJSONObject("entity");

        String razorpayPaymentId = paymentEntity.getString("id");
        String orderId = paymentEntity.getString("order_id");

        Payment payment = paymentRepository.findByRazorpayOrderId(orderId);

        if (payment == null) return;

        if (payment.isExpired()) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment order expired");
        }

        Milestone milestone = payment.getMilestone();

        switch (eventType) {
            case "payment.captured" -> {
                payment.setRazorpayPaymentId(razorpayPaymentId);
                payment.setStatus(PaymentStatus.ESCROW_HELD);
                payment.setCreatedAt(OffsetDateTime.now());
                paymentRepository.save(payment);

                milestone.setPaymentStatus(PaymentStatus.ESCROW_HELD);
                milestoneRepository.save(milestone);
            }
            case "payment.failed" -> {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);

                milestone.setPaymentStatus(PaymentStatus.FAILED);
                milestoneRepository.save(milestone);
            }
        }
    }

    /** Process freelancer fund account verification */
    public void processFundAccountWebhook(String payload, String signature) {
        try{
            verifySignature(payload, signature);
        }catch (Exception e){
            throw new RuntimeException("issue with webhook processing during freelancer account verification");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        if (!eventType.startsWith("fund_account.")) return;

        JSONObject fundEntity = event.getJSONObject("payload")
                .getJSONObject("fund_account")
                .getJSONObject("entity");

        String fundAccountId = fundEntity.getString("id");
        Freelancer freelancer = freelancerRepository.findByRazorpayFundAccountId(fundAccountId);

        if (freelancer == null) return;

        switch (eventType) {
            case "fund_account.verified" -> freelancer.setPayoutAccountStatus(PayoutAccountStatus.ACTIVE);
            case "fund_account.failed" -> freelancer.setPayoutAccountStatus(PayoutAccountStatus.FAILED);
        }

        freelancerRepository.save(freelancer);
    }

    public void setupPayoutAccount(String email, PayoutSetupRequest req) {

        User user = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("user not found"));
        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found"));

        if (freelancer.getPayoutAccountStatus() == PayoutAccountStatus.ACTIVE) {
            throw new RuntimeException("Payout already active");
        }

        String contactId = null;
        String fundAccountId = null;
        try {
            contactId = razorpayService.createContact(freelancer.getUser().getUsername(), freelancer.getUser().getEmail(), freelancer.getPhone());
            fundAccountId = razorpayService.createFundAccount(contactId, req.getAccountHolderName(), req.getAccountNumber(), req.getIfscCode());
        } catch (Exception e) {
            e.getMessage();
            throw new InternalServerErrorException("failed to save the payout status");
        }

        freelancer.setRazorpayContactId(contactId);
        freelancer.setRazorpayFundAccountId(fundAccountId);
        freelancer.setPayoutAccountStatus(PayoutAccountStatus.PENDING_VERIFICATION);

        freelancerRepository.save(freelancer);
    }

    @Transactional
    public MilestonePaymentResponse createPaymentOrder(Milestone milestone) {

        if (milestone.getPaymentStatus() == PaymentStatus.RELEASED) {
            throw new RuntimeException("Payment already completed for milestone");
        }

        List<Payment> existingPayments = paymentRepository.findByByMilestone(milestone);

        for (Payment payment : existingPayments) {
            if (payment.isExpired() && payment.getStatus() == PaymentStatus.NOT_PAID) {
                payment.setStatus(PaymentStatus.FAILED);
                paymentRepository.save(payment);
            }

            if (!payment.isExpired() && payment.getStatus() == PaymentStatus.NOT_PAID) {
                return MilestonePaymentResponse.from(payment, milestone, razorpayKey);
            }
        }

        RazorpayOrderResponse order;
        try {
            order = razorpayService.createOrder(milestone.getAmount(), milestone.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order", e);
        }

        Payment payment = new Payment();
        payment.setAmount(milestone.getAmount());
        payment.setCurrency("INR");
        payment.setRazorpayOrderId(order.getOrderId());
        payment.setExpiresAt(OffsetDateTime.now().plusMinutes(20));
        payment.setStatus(PaymentStatus.NOT_PAID);
        payment.setMilestone(milestone);
        payment.setPayer(milestone.getContract().getClient());
        payment.setPayee(milestone.getContract().getFreelancer());

        paymentRepository.save(payment);

        return MilestonePaymentResponse.from(payment, milestone, razorpayKey);
    }

    @Transactional
    public MilestonePaymentResponse verifyPayment(Map<String, String> request) {

        String orderId = request.get("razorpay_order_id");
        String paymentId = request.get("razorpay_payment_id");
        String signature = request.get("razorpay_signature");

        Payment payment = paymentRepository.findByRazorpayOrderId(orderId);

        if (payment == null) {
            throw new InvalidIdException("Invalid Razorpay order id");
        }

        if (payment.isExpired()) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Payment order expired");
        }

        if (payment.getStatus() == PaymentStatus.ESCROW_HELD) {
            return MilestonePaymentResponse.from(payment, payment.getMilestone(), razorpayKey);
        }

        boolean verified;
        try {
            verified = razorpayService.verifyPaymentSignature(orderId, paymentId, signature);
        } catch (Exception e) {
            throw new RuntimeException("Payment verification error", e);
        }

        if (!verified) {
            payment.setStatus(PaymentStatus.FAILED);
            paymentRepository.save(payment);
            throw new RuntimeException("Invalid payment signature");
        }

        payment.setRazorpayPaymentId(paymentId);
        payment.setCreatedAt(OffsetDateTime.now());
        payment.setStatus(PaymentStatus.ESCROW_HELD);
        paymentRepository.save(payment);

        Milestone milestone = payment.getMilestone();
        milestone.setPaymentStatus(PaymentStatus.ESCROW_HELD);
        milestoneRepository.save(milestone);

        return MilestonePaymentResponse.from(payment, milestone, razorpayKey);
    }

    @Transactional
    public void releaseMilestonePayment(Milestone milestone) {

        if (milestone.getPaymentStatus() != PaymentStatus.ESCROW_HELD) {
            throw new RuntimeException("Escrow payment not available");
        }

        Payment payment = paymentRepository.findByMilestoneAndStatus(milestone, PaymentStatus.ESCROW_HELD);
        if(!payment.getPayee().getPayoutAccountStatus().equals(PayoutAccountStatus.ACTIVE)){
            throw new InvalidIdException("freelancer account is not verified");
        }
        JSONObject transfer = new JSONObject();
        transfer.put("account", milestone.getContract().getFreelancer().getRazorpayFundAccountId());
        transfer.put("account", milestone.getContract().getFreelancer());
        transfer.put("amount", BigDecimal.valueOf(payment.getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        transfer.put("currency", "INR");
        transfer.put("notes", new JSONObject().put("milestoneId", milestone.getId()));

        JSONObject request = new JSONObject();
        request.put("transfers", new JSONArray().put(transfer));

        try {
            razorpayService.transfer(payment.getRazorpayPaymentId(), request);
        } catch (Exception e) {
            throw new RuntimeException("Payout failed", e);
        }

        payment.setStatus(PaymentStatus.RELEASED);
        milestone.setPaymentStatus(PaymentStatus.RELEASED);
        milestone.setStatus(MilestoneStatus.COMPLETED);

        Contract contract = milestone.getContract();
        Milestone lastMilestone = milestoneService.getLastMilestone(milestone.getContract());

        if(lastMilestone.getId() == milestone.getId()){
            contract.setStatus(ContractStatus.COMPLETED);
        }

        paymentRepository.save(payment);
        milestoneRepository.save(milestone);
        contractRepository.save(contract);
    }

    @Transactional
    public void refundMilestonePayment(Milestone milestone, String reason) {

        if (milestone.getPaymentStatus() != PaymentStatus.ESCROW_HELD) {
            throw new RuntimeException("Refund not allowed. Payment already released or not paid.");
        }

        Payment payment = paymentRepository.findByMilestoneAndStatus(milestone, PaymentStatus.ESCROW_HELD);

        try {
            razorpayService.refundPayment(payment.getRazorpayPaymentId(), (int) (payment.getAmount() * 100));
        } catch (Exception e) {
            throw new RuntimeException("Refund failed", e);
        }

        Contract contract = milestone.getContract();
        contract.setStatus(ContractStatus.CANCELLED);
        payment.setStatus(PaymentStatus.REFUNDED);

        milestone.setPaymentStatus(PaymentStatus.REFUNDED);
        milestone.setStatus(MilestoneStatus.CANCELLED);

        paymentRepository.save(payment);
        milestoneRepository.save(milestone);
        contractRepository.save(contract);
    }

}