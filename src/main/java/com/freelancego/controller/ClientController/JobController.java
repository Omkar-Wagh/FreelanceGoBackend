package com.freelancego.controller.ClientController;

import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.user.ContractDto;
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

    @PostMapping("/create-post")
    ResponseEntity<JobDto> createPost(@RequestBody JobDto jobDto,Authentication auth){
        return ResponseEntity.ok(jobService.createPost(jobDto,auth.getName()));
    }

    @GetMapping("/get-post")
    ResponseEntity<Page<JobDto>> getPost(@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "5") int size,Authentication auth){
        return ResponseEntity.ok(jobService.getPostByClient(page, size, auth.getName()));
    }

    @GetMapping("/get-post/{id}")
    ResponseEntity<Map<String,Object>> getPostById(@PathVariable("id") int id, Authentication auth){
        return ResponseEntity.ok(jobService.getPostById(id,auth.getName()));
    }

    @GetMapping("/get-post-in-progress")
    ResponseEntity<Map<String,Object>> getDashboardInfo(Authentication auth){
        return ResponseEntity.ok(jobService.getDashboardData(auth.getName()));
    }

    @GetMapping("/get-in-progress-post")
    ResponseEntity<List<ContractDto>> getPostByPhase(Authentication auth){
        return ResponseEntity.ok(jobService.getPostByPhase(auth.getName()));
    }

}
