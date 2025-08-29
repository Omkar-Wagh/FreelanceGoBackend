package com.freelancego.controller.ClientController;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.service.ClientService.ClientService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ClientController {

    final private ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    //    @PreAuthorize("hasRole('CLIENT')")
    @PostMapping("/create-profile/client")
    ResponseEntity<Map<String,Object>> createClientProfile(@RequestBody ClientDto clientDto, Authentication auth){
        return ResponseEntity.ok(clientService.createClient(clientDto, auth.getName()));
    }

}
