package com.freelancego.controller.FreelancerController;

import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.service.FreelancerService.BidService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class BidController {

    final private BidService bidService;

    public BidController(BidService bidService) {
        this.bidService = bidService;
    }

    @PostMapping("/create-bid")
    ResponseEntity<BidDto> createBid(@RequestBody BidDto bidDto, Authentication auth){
        return ResponseEntity.ok(bidService.createBid(bidDto,auth.getName()));
    }
    @PostMapping("/update-bid")
    ResponseEntity<BidDto> updateBid(@RequestBody BidDto bidDto, Authentication auth){
        return ResponseEntity.ok(bidService.updateBid(bidDto,auth.getName()));
    }

}
