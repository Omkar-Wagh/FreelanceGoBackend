package com.freelancego.service.payment;

import com.freelancego.dto.freelancer.PayoutSetupRequest;
import com.freelancego.dto.user.MilestonePaymentResponse;
import com.freelancego.model.Milestone;

import java.util.Map;

public interface PaymentService {
    MilestonePaymentResponse createPaymentOrder(Milestone milestone);

    MilestonePaymentResponse verifyPayment(Map<String, String> request);

    void releaseMilestonePayment(Milestone milestone);

    void setupPayoutAccount(String name, PayoutSetupRequest request);

    void processPaymentWebhook(String payload, String signature);

    void processFundAccountWebhook(String payload, String signature);
}
