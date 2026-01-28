package com.freelancego.service.payment.impl;

import com.freelancego.dto.user.MilestonePaymentResponse;
import com.freelancego.dto.user.RazorpayOrderResponse;
import com.freelancego.enums.MilestoneStatus;
import com.freelancego.enums.PaymentStatus;
import com.freelancego.exception.InvalidIdException;
import com.freelancego.model.Milestone;
import com.freelancego.model.Payment;
import com.freelancego.repo.MilestoneRepository;
import com.freelancego.repo.PaymentRepository;
import com.freelancego.service.payment.PaymentService;
import jakarta.transaction.Transactional;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Map;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MilestoneRepository milestoneRepository;
    private final RazorpayService razorpayService;

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    public PaymentServiceImpl(PaymentRepository paymentRepository, MilestoneRepository milestoneRepository, RazorpayService razorpayService) {
        this.paymentRepository = paymentRepository;
        this.milestoneRepository = milestoneRepository;
        this.razorpayService = razorpayService;
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
        payment.setPayer(milestone.getContract().getClient().getUser());
        payment.setPayee(milestone.getContract().getFreelancer().getUser());

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
        JSONObject transfer = new JSONObject();
        // freelancerAccountId needed
//        transfer.put("account", milestone.getContract().getFreelancer().getRazorpayAccountId());
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

        paymentRepository.save(payment);
        milestoneRepository.save(milestone);
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

        payment.setStatus(PaymentStatus.REFUNDED);

        milestone.setPaymentStatus(PaymentStatus.REFUNDED);
        milestone.setStatus(MilestoneStatus.CANCELLED);

        paymentRepository.save(payment);
        milestoneRepository.save(milestone);
    }

}
