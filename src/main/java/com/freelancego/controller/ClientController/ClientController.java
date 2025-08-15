package com.freelancego.controller.ClientController;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.service.ClientService.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ClientController {

    @Autowired
    private ClientService clientService;

    @PostMapping("/create-profile/client")
    ResponseEntity<?> createClientProfile(@RequestBody ClientDto clientDto, Authentication auth){
        return clientService.createClient(clientDto, auth.getName());
    }
}
