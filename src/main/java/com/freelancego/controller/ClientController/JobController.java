package com.freelancego.controller.ClientController;

import com.freelancego.dto.client.JobDto;
import com.freelancego.service.ClientService.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
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
    ResponseEntity<List<JobDto>> getPost(Authentication auth){
        return ResponseEntity.ok(jobService.getPostByClient(auth.getName()));
    }
    @GetMapping("/get-post/id")
    ResponseEntity<JobDto> getPostById(@PathVariable("id") int id, Authentication auth){
        return ResponseEntity.ok(jobService.getPostById(id,auth.getName()));
    }

}
