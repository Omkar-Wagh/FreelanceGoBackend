package com.freelancego.service.payment.impl;

import com.freelancego.dto.freelancer.PayoutSetupRequest;
import com.freelancego.dto.user.MilestonePaymentResponse;
import com.freelancego.dto.user.RazorpayOrderResponse;
import com.freelancego.enums.ContractStatus;
import com.freelancego.enums.MilestoneStatus;
import com.freelancego.enums.PaymentStatus;
import com.freelancego.enums.PayoutAccountStatus;
import com.freelancego.exception.*;
import com.freelancego.model.*;
import com.freelancego.repo.*;
import com.freelancego.service.payment.PaymentService;
import com.razorpay.Transfer;
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
import java.util.Comparator;
import java.util.List;

@Service
public class PaymentServiceImpl implements PaymentService {

    private final PaymentRepository paymentRepository;
    private final MilestoneRepository milestoneRepository;
    private final RazorpayService razorpayService;
    private final FreelancerRepository freelancerRepository;
    private final UserRepository userRepository;
    private final ContractRepository contractRepository;

    @Value("${razorpay.key.id}")
    private String razorpayKey;

    @Value("${razorpay.webhook.secret}")
    private String webhookSecret;

    public PaymentServiceImpl(PaymentRepository paymentRepository, MilestoneRepository milestoneRepository, RazorpayService razorpayService, FreelancerRepository freelancerRepository, UserRepository userRepository, ContractRepository contractRepository) {
        this.paymentRepository = paymentRepository;
        this.milestoneRepository = milestoneRepository;
        this.razorpayService = razorpayService;
        this.freelancerRepository = freelancerRepository;
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
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

    @Transactional
    public void processPaymentWebhook(String payload, String signature) {

        try {
            verifySignature(payload, signature);
        } catch (Exception e) {
            throw new RuntimeException("Invalid Razorpay signature");
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

        Milestone milestone = payment.getMilestone();

        switch (eventType) {

            case "payment.captured" -> {

                if (payment.getStatus() == PaymentStatus.ESCROW_HELD ||
                        payment.getStatus() == PaymentStatus.COMPLETED) {
                    return;
                }

                payment.setRazorpayPaymentId(razorpayPaymentId);
                payment.setStatus(PaymentStatus.ESCROW_HELD);
                payment.setCreatedAt(OffsetDateTime.now());
                paymentRepository.save(payment);

                if (milestone.getPaymentStatus() != PaymentStatus.ESCROW_HELD) {
                    milestone.setPaymentStatus(PaymentStatus.ESCROW_HELD);
                    milestone.setLocked(true);
                    milestone.setStatus(MilestoneStatus.IN_PROGRESS);
                    milestoneRepository.save(milestone);
                }
            }

            case "payment.failed" -> {

                if (payment.getStatus() == PaymentStatus.ESCROW_HELD ||
                        payment.getStatus() == PaymentStatus.COMPLETED) {
                    return;
                }

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
            case "fund_account.validation.completed" -> freelancer.setPayoutAccountStatus(PayoutAccountStatus.ACTIVE);
            case "fund_account.validation.failed" -> freelancer.setPayoutAccountStatus(PayoutAccountStatus.FAILED);
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

        if (milestone.getPaymentStatus() == PaymentStatus.COMPLETED) {
            throw new RuntimeException("Payment already completed for milestone");
        }

        if (milestone.getPaymentStatus() == PaymentStatus.ESCROW_HELD) {
            throw new RuntimeException("you have made payment already completed for milestone");
        }

        Payment payment = paymentRepository.findByMilestone(milestone);

        if (payment != null) {
            if (payment.getStatus() == PaymentStatus.NOT_PAID) {
                return MilestonePaymentResponse.from(payment, milestone, razorpayKey);
            }
        } else {
            payment = new Payment();
            payment.setMilestone(milestone);
            payment.setPayer(milestone.getContract().getClient());
            payment.setPayee(milestone.getContract().getFreelancer());
            payment.setCurrency("INR");
            payment.setAmount(milestone.getAmount());
        }

        RazorpayOrderResponse order;
        try {
            order = razorpayService.createOrder(milestone.getAmount(), milestone.getId());
        } catch (Exception e) {
            throw new RuntimeException("Failed to create Razorpay order", e);
        }

        payment.setRazorpayOrderId(order.getOrderId());
        payment.setStatus(PaymentStatus.NOT_PAID);
        payment.setExpiresAt(OffsetDateTime.now().plusMinutes(20)); // optional, just for temporary expiry check

        paymentRepository.save(payment);

        return MilestonePaymentResponse.from(payment, milestone, razorpayKey);
    }

    @Transactional
    public void releaseMilestonePayment(Milestone milestone) {

        if (milestone.getPaymentStatus() != PaymentStatus.ESCROW_HELD) {
            throw new RuntimeException("Escrow payment not available");
        }

        Payment payment = paymentRepository.findByMilestoneAndStatus(milestone, PaymentStatus.ESCROW_HELD);

        if (payment == null) {
            throw new RuntimeException("No escrow payment found");
        }

        if (payment.getPayee().getPayoutAccountStatus() != PayoutAccountStatus.ACTIVE) {
            throw new InvalidIdException("Freelancer payout account not verified");
        }

        if (payment.getStatus() == PaymentStatus.RELEASED || payment.getStatus() == PaymentStatus.COMPLETED) {
            throw new ConflictException("Payment already released");
        }

        JSONObject transfer = new JSONObject();
        transfer.put("account", payment.getPayee().getRazorpayFundAccountId());
        transfer.put("amount", BigDecimal.valueOf(payment.getAmount()).multiply(BigDecimal.valueOf(100)).intValue());
        transfer.put("currency", "INR");
        transfer.put("notes", new JSONObject().put("milestoneId", milestone.getId()));

        JSONObject request = new JSONObject();
        request.put("transfers", new JSONArray().put(transfer));

        List<Transfer> transfers;
        try {
            transfers = razorpayService.transfer(payment.getRazorpayPaymentId(), request);
        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate payout", e);
        }

        Transfer t = transfers.get(0);

        payment.setRazorpayTransferId(t.get("id").toString());
        payment.setStatus(PaymentStatus.RELEASED);

        milestone.setPaymentStatus(PaymentStatus.RELEASED);

        paymentRepository.save(payment);
        milestoneRepository.save(milestone);
    }

    @Transactional
    public void processTransferWebhook(String payload, String signature) {

        try{
            verifySignature(payload, signature);
        }catch (Exception e){
            throw new RuntimeException("issue with webhook processing during platform to freelancer payment");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        if (!eventType.startsWith("transfer.")) return;

        JSONObject transfer = event.getJSONObject("payload")
                .getJSONObject("transfer")
                .getJSONObject("entity");

        String transferId = transfer.getString("id");
        String status = transfer.getString("status");

        Payment payment = paymentRepository.findByRazorpayTransferId(transferId);

        if (payment == null) return;

        Milestone milestone = payment.getMilestone();
        Contract contract = milestone.getContract();

        switch (status) {
            case "processed" -> {
                payment.setStatus(PaymentStatus.COMPLETED);

                if(getLastMilestone(contract).getId() == milestone.getId()){
                    milestone.setStatus(MilestoneStatus.COMPLETED);
                    contract.setStatus(ContractStatus.COMPLETED);
                }

            }
            case "failed" -> {
                payment.setStatus(PaymentStatus.FAILED);
            }
            case "pending", "queued", "processing" -> {
                return;
            }
        }

        paymentRepository.save(payment);
        milestoneRepository.save(milestone);
        contractRepository.save(contract);
    }

    @Transactional
    public void refundMilestonePayment(Milestone milestone, String reason) {

        if (milestone.getPaymentStatus() != PaymentStatus.ESCROW_HELD) {
            throw new RuntimeException("Refund not allowed in current state");
        }

        Payment payment = paymentRepository
                .findByMilestoneAndStatus(milestone, PaymentStatus.ESCROW_HELD);

        if (payment == null) {
            throw new RuntimeException("No escrow payment found");
        }

        if (payment.getStatus() == PaymentStatus.RELEASED || payment.getStatus() == PaymentStatus.REFUNDED) {
            throw new ConflictException("Refund already initiated");
        }

        try {
            razorpayService.refundPayment(payment.getRazorpayPaymentId(), payment.getAmount());
        } catch (Exception e) {
            throw new RuntimeException("Failed to initiate refund", e);
        }

        paymentRepository.save(payment);
        milestoneRepository.save(milestone);
    }

    @Transactional
    public void processRefundWebhook(String payload, String signature) {

        try{
            verifySignature(payload, signature);
        }catch (Exception e){
            throw new RuntimeException("issue with webhook processing during platform to freelancer payment");
        }

        JSONObject event = new JSONObject(payload);
        String eventType = event.getString("event");

        if (!eventType.startsWith("refund.")) return;

        JSONObject refund = event.getJSONObject("payload")
                .getJSONObject("refund")
                .getJSONObject("entity");

        String paymentId = refund.getString("payment_id");
        String status = refund.getString("status");

        Payment payment = paymentRepository.findByRazorpayPaymentId(paymentId);

        if (payment == null) return;

        Milestone milestone = payment.getMilestone();
        Contract contract = milestone.getContract();

        switch (status) {

            case "processed" -> {
                payment.setStatus(PaymentStatus.REFUNDED);
                milestone.setPaymentStatus(PaymentStatus.REFUNDED);
                milestone.setStatus(MilestoneStatus.CANCELLED);
                contract.setStatus(ContractStatus.CANCELLED);
            }

            case "failed" -> {
                payment.setStatus(PaymentStatus.FAILED);
            }
        }

        paymentRepository.save(payment);
        milestoneRepository.save(milestone);
        contractRepository.save(contract);
    }

    private Milestone getLastMilestone(Contract contract) {
        List<Milestone> milestones = milestoneRepository.findByContract(contract);
        return milestones.stream().max(Comparator.comparingInt(Milestone::getMilestoneNumber))
                .orElseThrow(() -> new RuntimeException("No milestones found for this contract"));
    }

}