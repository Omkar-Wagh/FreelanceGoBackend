package com.freelancego.controller.MilestoneController;

import com.freelancego.dto.user.MilestoneDto;
import com.freelancego.service.MilestoneService.MilestoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/")
public class MilestoneController {
    final private MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
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

    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/client-approval")
    ResponseEntity<MilestoneDto> approveMilestone(@RequestParam int milestoneId, @RequestParam int clientId, Authentication auth){
        return ResponseEntity.ok(milestoneService.approveMilestone(milestoneId,clientId,auth.getName()));
    }

}
