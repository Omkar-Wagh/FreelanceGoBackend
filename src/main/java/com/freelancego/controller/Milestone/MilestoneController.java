package com.freelancego.controller.Milestone;

import com.freelancego.dto.user.MilestoneDto;
import com.freelancego.dto.user.MilestonePaymentResponse;
import com.freelancego.dto.user.SubmissionDto;
import com.freelancego.service.Milestone.MilestoneService;
import com.freelancego.service.payment.PaymentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/")
public class MilestoneController {
    final private MilestoneService milestoneService;
    final private PaymentService paymentService;

    public MilestoneController(MilestoneService milestoneService, PaymentService paymentService) {
        this.milestoneService = milestoneService;
        this.paymentService = paymentService;
    }

    @GetMapping("/get-milestone/{contractId}")
    ResponseEntity<List<MilestoneDto>> getMileStones(@PathVariable("contractId") int contractId, Authentication auth){
        return ResponseEntity.ok(milestoneService.getMileStone(contractId,auth.getName()));
    }

    @PostMapping("/create-milestone")
    ResponseEntity<MilestoneDto> createMilestone(@RequestBody MilestoneDto milestoneDto,@RequestParam int clientId, int freelancerId, Authentication auth){
        return ResponseEntity.ok(milestoneService.createMilestone(milestoneDto,clientId,freelancerId,auth.getName()));
    }

    @PostMapping("/update-milestone")
    ResponseEntity<MilestoneDto> updateMilestone(@RequestBody MilestoneDto milestoneDto, int freelancerId, Authentication auth){
        return  ResponseEntity.ok(milestoneService.updateMilestone(milestoneDto,freelancerId,auth.getName()));
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/client-feedback")
    ResponseEntity<MilestoneDto> editMilestone(@RequestBody MilestoneDto milestoneDto, @RequestParam int clientId, Authentication auth){
        return ResponseEntity.ok(milestoneService.editMilestone(milestoneDto,clientId,auth.getName()));
    }

    // trigger payment from here
    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/milestone-approval")
    ResponseEntity<MilestonePaymentResponse> approveMilestone(@RequestParam int milestoneId, @RequestParam int clientId, Authentication auth){
        return ResponseEntity.ok(milestoneService.approveMilestone(milestoneId,clientId,auth.getName()));
    }

    @PostMapping("/verify")
    public ResponseEntity<MilestonePaymentResponse> verifyPayment(@RequestBody Map<String, String> request) {
            return ResponseEntity.ok(paymentService.verifyPayment(request));
    }

    @GetMapping("/get-submission")
    ResponseEntity<SubmissionDto> getSubmission(@RequestParam int milestoneId,@RequestParam int clientId, Authentication auth){
        return ResponseEntity.ok(milestoneService.getSubmission(milestoneId,clientId,auth.getName()));
    }

    @PostMapping("/create-submission")
    ResponseEntity<SubmissionDto> createSubmission(@RequestPart(value = "submission") SubmissionDto submissionDto,
                                                   @RequestPart(value = "file", required = false) MultipartFile file, @RequestParam int milestoneId,
                                                   @RequestParam int freelancerId, Authentication auth){
        return ResponseEntity.ok(milestoneService.createSubmission(submissionDto,file,milestoneId,freelancerId,auth.getName()));
    }

    @PostMapping("/update-submission")
    ResponseEntity<SubmissionDto> updateSubmission(@RequestPart(value = "submission") SubmissionDto submissionDto,
                                                   @RequestPart(value = "file", required = false) MultipartFile file, @RequestParam int milestoneId,
                                                   @RequestParam int freelancerId, Authentication auth){
        return ResponseEntity.ok(milestoneService.updateSubmission(submissionDto,file,milestoneId,freelancerId,auth.getName()));
    }

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/client-remark")
    ResponseEntity<SubmissionDto> editSubmission(@RequestBody SubmissionDto submissionDto, @RequestParam int clientId, Authentication auth){
        return ResponseEntity.ok(milestoneService.editSubmission(submissionDto,clientId,auth.getName()));
    }

    @PostMapping("/submission-approval")
    ResponseEntity<MilestoneDto> approveSubmission(@RequestParam int submissionId, @RequestParam int clientId, Authentication auth){
        return ResponseEntity.ok(milestoneService.approveSubmission(submissionId,clientId,auth.getName()));
    }

}
