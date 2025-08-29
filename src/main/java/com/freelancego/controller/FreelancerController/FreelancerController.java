package com.freelancego.controller.FreelancerController;

import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.service.FreelancerService.FreelancerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api")
public class FreelancerController {

    final private FreelancerService freelancerService;

    public FreelancerController(FreelancerService freelancerService) {
        this.freelancerService = freelancerService;
    }

    @PostMapping("/create-profile/freelancer")
    ResponseEntity<Map<String,Object>> createFreelancerProfile(@RequestBody FreelancerDto freelancerDto, Authentication auth){
        return ResponseEntity.ok(freelancerService.createFreelancer(freelancerDto,auth.getName()));
    }

//    @PostMapping("/create-bid")
//    ResponseEntity<?> createBid(@RequestBody BidDto bidDto, Authentication auth){
//        return freelancerService.createBid();
//    }
}
