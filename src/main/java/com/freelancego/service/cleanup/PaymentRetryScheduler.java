package com.freelancego.service.cleanup;

import com.freelancego.enums.PaymentStatus;
import com.freelancego.model.Milestone;
import com.freelancego.model.Payment;
import com.freelancego.repo.MilestoneRepository;
import com.freelancego.repo.PaymentRepository;
import com.freelancego.service.payment.impl.RazorpayService;
import com.razorpay.Transfer;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
public class PaymentRetryScheduler {

    private final PaymentRepository paymentRepository;
    private final RazorpayService razorpayService;
    private final MilestoneRepository milestoneRepository;

    public PaymentRetryScheduler(PaymentRepository paymentRepository, RazorpayService razorpayService, MilestoneRepository milestoneRepository) {
        this.paymentRepository = paymentRepository;
        this.razorpayService = razorpayService;
        this.milestoneRepository = milestoneRepository;
    }

    // Runs once every 24 hours
    @Scheduled(fixedRate = 86400000)
    public void retryFailedPayments() {
        List<Payment> failedPayments = paymentRepository.findByStatus(PaymentStatus.FAILED);

        for (Payment payment : failedPayments) {
            try {
                makeTransfer(payment);
            } catch (Exception e) {
                System.out.println("Retry failed for payment: " + payment.getId());
            }
        }
    }

    private void makeTransfer(Payment payment) throws Exception {
        JSONObject transfer = new JSONObject();
        transfer.put("account", payment.getPayee().getRazorpayFundAccountId());
        transfer.put("amount", (int) (payment.getAmount() * 100));
        transfer.put("currency", "INR");
        transfer.put("notes", new JSONObject().put("milestoneId", payment.getMilestone().getId()));

        JSONObject request = new JSONObject();
        request.put("transfers", new JSONArray().put(transfer));

        List<Transfer> transfers = razorpayService.transfer(payment.getRazorpayPaymentId(), request);

        payment.setStatus(PaymentStatus.RELEASED);
        paymentRepository.save(payment);

        Milestone milestone = payment.getMilestone();
        milestone.setPaymentStatus(PaymentStatus.RELEASED);
        milestoneRepository.save(milestone);
    }
}
