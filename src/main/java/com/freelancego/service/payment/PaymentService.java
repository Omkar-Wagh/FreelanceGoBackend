package com.freelancego.service.payment;

import com.freelancego.dto.freelancer.PayoutSetupRequest;
import com.freelancego.dto.user.MilestonePaymentResponse;
import com.freelancego.model.Milestone;


public interface PaymentService {
    MilestonePaymentResponse createPaymentOrder(Milestone milestone);

    void releaseMilestonePayment(Milestone milestone);

    void setupPayoutAccount(String name, PayoutSetupRequest request);

    void processPaymentWebhook(String payload, String signature);

    void processFundAccountWebhook(String payload, String signature);
}
