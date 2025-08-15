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

@RestController
@RequestMapping("/api")
public class FreelancerController {

    @Autowired
    private FreelancerService freelancerService;

    @PostMapping("/create-profile/freelancer")
    ResponseEntity<?> createFreelancerProfile(@RequestBody FreelancerDto freelancerDto, Authentication auth){
        return freelancerService.createFreelancer(freelancerDto,auth.getName());
    }
}
