package com.freelancego.controller.MilestoneController;

import com.freelancego.service.MilestoneService.MilestoneService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/")
public class MilestoneController {
    final private MilestoneService milestoneService;

    public MilestoneController(MilestoneService milestoneService) {
        this.milestoneService = milestoneService;
    }

    @GetMapping("/get-milestone")
    ResponseEntity<?> getMileStones(Authentication auth){
        return ResponseEntity.ok(milestoneService.getMileStone(auth.getName()));
    }
}
