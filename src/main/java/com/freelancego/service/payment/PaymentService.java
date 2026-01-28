package com.freelancego.service.payment;

import com.freelancego.dto.user.MilestonePaymentResponse;
import com.freelancego.model.Milestone;
import com.freelancego.model.User;

import java.util.Map;

public interface PaymentService {
    MilestonePaymentResponse createPaymentOrder(Milestone milestone);

    MilestonePaymentResponse verifyPayment(Map<String, String> request);

    void releaseMilestonePayment(Milestone milestone);
}
