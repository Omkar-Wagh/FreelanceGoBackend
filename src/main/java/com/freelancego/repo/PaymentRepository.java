package com.freelancego.repo;

import com.freelancego.enums.PaymentStatus;
import com.freelancego.model.Milestone;
import com.freelancego.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    Payment findByRazorpayOrderId(String orderId);

    Payment findByMilestoneAndStatus(Milestone milestone, PaymentStatus paymentStatus);

    Payment findByRazorpayTransferId(String transferId);

    Payment findByMilestone(Milestone milestone);

    Payment findByRazorpayPaymentId(String paymentId);
}
