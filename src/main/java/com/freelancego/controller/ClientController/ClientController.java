package com.freelancego.controller.ClientController;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.dto.client.JobDto;
import com.freelancego.service.ClientService.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientService clientService;

//    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/create-profile/client")
    ResponseEntity<?> createClientProfile(@RequestBody ClientDto clientDto, Authentication auth){
        return clientService.createClient(clientDto, auth.getName());
    }

    @PostMapping("/create-post")
    ResponseEntity<?> createPost(@RequestBody JobDto jobDto,Authentication auth){
        return clientService.createPost(jobDto,auth.getName());
    }

    @GetMapping("/get-post")
    ResponseEntity<?> getPost(Authentication auth){
        return clientService.getPostByClient(auth.getName());
    }

}
