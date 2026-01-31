package com.freelancego.controller.Milestone;

import com.freelancego.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/webhooks/razorpay")
public class RazorpayWebhookController {

    private final PaymentService paymentService;

    public RazorpayWebhookController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    /** Webhook for client → platform payments */
    @PostMapping("/payment/verify")
    public ResponseEntity<String> handlePaymentWebhook(@RequestHeader("X-Razorpay-Signature") String signature, @RequestBody String payload) {
        try {
            paymentService.processPaymentWebhook(payload, signature);
            return ResponseEntity.ok("Payment webhook processed");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /** Webhook for freelancer fund account verification */
    @PostMapping("/fund-account")
    public ResponseEntity<String> handleFundAccountWebhook(@RequestHeader("X-Razorpay-Signature") String signature, @RequestBody String payload) {
        try {
            paymentService.processFundAccountWebhook(payload, signature);
            return ResponseEntity.ok("Fund account webhook processed");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    /** Webhook for confirming payment status from platform to freelancer account*/
    @PostMapping("/payment/transfer")
    public ResponseEntity<String> handleTransferWebhook(@RequestHeader("X-Razorpay-Signature") String signature, @RequestBody String payload) {
        try {
            paymentService.processTransferWebhook(payload, signature);
            return ResponseEntity.ok("transfer webhook processed");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Error: " + e.getMessage());
        }
    }

    @PostMapping("/payment/refund")
    public ResponseEntity<String> handleRefundWebhook(@RequestHeader("X-Razorpay-Signature") String signature, @RequestBody String payload) {

        paymentService.processRefundWebhook(payload, signature);
        return ResponseEntity.ok("refund webhook processed");
    }

}
