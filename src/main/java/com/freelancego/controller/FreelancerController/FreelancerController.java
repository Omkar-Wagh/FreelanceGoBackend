package com.freelancego.controller.FreelancerController;

import com.freelancego.dto.freelancer.BrowseJobDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.service.FreelancerService.FreelancerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @GetMapping("/browse-job/")
    ResponseEntity<List<BrowseJobDto>> getBrowseJobs(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "5") int size, Authentication auth){
        return ResponseEntity.ok(freelancerService.getBrowseJobs(page,size,auth.getName()));
    }

}
