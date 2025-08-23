package com.freelancego.controller.ClientController;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.service.ClientService.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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

//    @GetMapping("/change-user-role")
//    ResponseEntity<?> changeUserRole(@RequestBody Map<String, String> request, @RequestHeader("Authorization") String authHeader){
//        return clientService.changeRole(request,authHeader);
//    }
}
