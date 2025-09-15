package com.freelancego.controller.FreelancerController;

import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.service.FreelancerService.FreelancerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BidController {

    final private FreelancerService freelancerService;

    public BidController(FreelancerService freelancerService) {
        this.freelancerService = freelancerService;
    }

//    @PostMapping("/create-bid")
//    ResponseEntity<?> createBid(@RequestBody BidDto bidDto, Authentication auth){
//        return freelancerService.createBid();
//    }

}
