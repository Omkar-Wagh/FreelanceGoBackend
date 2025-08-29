package com.freelancego.controller.FreelancerController;

import com.freelancego.service.FreelancerService.FreelancerService;
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
