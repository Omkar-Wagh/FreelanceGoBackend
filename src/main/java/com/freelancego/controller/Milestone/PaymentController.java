package com.freelancego.controller.Milestone;

import com.freelancego.dto.freelancer.PayoutSetupRequest;
import com.freelancego.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/freelancer/payout")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/setup")
    public ResponseEntity<?> setupPayout(@RequestBody PayoutSetupRequest request, Authentication auth) {
        paymentService.setupPayoutAccount(auth.getName(), request);
        return ResponseEntity.ok("Payout setup initiated");
    }
}
