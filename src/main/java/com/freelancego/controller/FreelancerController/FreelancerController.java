package com.freelancego.controller.FreelancerController;

import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.freelancer.BrowseJobDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.dto.user.ContractDto;
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

    // Profile Creation
    @PostMapping("/create-profile/freelancer")
    ResponseEntity<Map<String,Object>> createFreelancerProfile(@RequestBody FreelancerDto freelancerDto, Authentication auth){
        return ResponseEntity.ok(freelancerService.createFreelancer(freelancerDto,auth.getName()));
    }

    // Browse Jobs
    @GetMapping("/browse-job")
    ResponseEntity<List<BrowseJobDto>> getBrowseJobs(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "5") int size, Authentication auth){
        return ResponseEntity.ok(freelancerService.getBrowseJobs(page,size,auth.getName()));
    }


    // My Proposals
    @GetMapping("/review-my-proposals")
    ResponseEntity<List<JobDto>> getJobByStatus(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size, Authentication auth){
        return ResponseEntity.ok(freelancerService.getPostByStatus(page, size, auth.getName()));
    }

    // Active Post
    @GetMapping("/active-projects-for-freelancer")
    ResponseEntity<List<ContractDto>> getPostByPhase(Authentication auth){
        return ResponseEntity.ok(freelancerService.getPostByPhase(auth.getName()));
    }

    // Bid History
    @GetMapping("/bid-history")
    ResponseEntity<Map<String, Object>> getBidHistoryOfFreelancer(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size, Authentication auth){
        return ResponseEntity.ok(freelancerService.getBidHistory(page,size,auth.getName()));
    }

    // Earnings
    @GetMapping("/earnings-dashboard")
    ResponseEntity<Map<String, Object>> getEarnings(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size, Authentication auth){
        return  ResponseEntity.ok(freelancerService.getEarningsDashboard(page,size,auth.getName()));
    }

    // Analytics
    @GetMapping("/freelancer-analytics")
    ResponseEntity<Map<String, Object>> getAnalyticsData(Authentication auth){
        return ResponseEntity.ok(freelancerService.getAnalytics(auth.getName()));
    }
}
