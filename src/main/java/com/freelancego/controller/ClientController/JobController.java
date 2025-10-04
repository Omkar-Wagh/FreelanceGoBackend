package com.freelancego.controller.ClientController;

import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.user.ContractDto;
import com.freelancego.model.Contract;
import com.freelancego.service.ClientService.JobService;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/dashboard/")
public class JobController {

    final private JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }

    // Post a Job
    @PostMapping("/create-post")
    ResponseEntity<JobDto> createPost(@RequestBody JobDto jobDto,Authentication auth){
        return ResponseEntity.ok(jobService.createPost(jobDto,auth.getName()));
    }

    // My Job Posts
    @GetMapping("/get-post")
    ResponseEntity<Page<JobDto>> getPost(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size,Authentication auth){
        return ResponseEntity.ok(jobService.getPostByClient(page, size, auth.getName()));
    }

    // Single Job Post
    @GetMapping("/get-post/{id}")
    ResponseEntity<Map<String,Object>> getPostById(@PathVariable("id") int id, Authentication auth){
        return ResponseEntity.ok(jobService.getPostById(id,auth.getName()));
    }

    // Dashboard data
    @GetMapping("/get-post-in-progress")
    ResponseEntity<Map<String,Object>> getDashboardInfo(Authentication auth){
        return ResponseEntity.ok(jobService.getDashboardData(auth.getName()));
    }

    // Active Post
    @GetMapping("/get-in-progress-post")
    ResponseEntity<List<ContractDto>> getPostByPhase(Authentication auth){
        return ResponseEntity.ok(jobService.getPostByPhase(auth.getName()));
    }

    // Review Proposals
    @GetMapping("/review-proposals")
    ResponseEntity<Page<JobDto>> getJobByStatus(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "5") int size,Authentication auth){
        return ResponseEntity.ok(jobService.getPostByStatus(page, size, auth.getName()));
    }
    // Hired Freelancer
    @GetMapping("/hired-freelancers")
    ResponseEntity<Page<ContractDto>> getHiredFreelancer(@RequestParam(defaultValue = "0") int page,
                                                         @RequestParam(defaultValue = "5") int size,Authentication auth){
        return ResponseEntity.ok(jobService.getHiredFreelancer(page, size, auth.getName()));
    }

}
