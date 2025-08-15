package com.freelancego.service.ClientService;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.enums.Role;
import com.freelancego.model.Client;
import com.freelancego.model.User;
import com.freelancego.repo.ClientRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.UserService.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ClientService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private JWTService jwtService;


    public ResponseEntity<?> createClient(ClientDto clientDto, String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        Client existingClient = clientRepository.findByUser(user);
        if (existingClient != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Freelancer profile already exists"));
        }
        Client newClient = new Client();
        newClient.setUser(user);
        newClient.setCompanyName(clientDto.companyName());
        newClient.setCompanyUrl(clientDto.companyUrl());
        newClient.setBio(clientDto.bio());
        newClient.setPhone(clientDto.phone());
        clientRepository.save(newClient);
        user.setRole(Role.CLIENT);
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail(),Role.CLIENT.name());
        return ResponseEntity.ok(
                Map.of(
                        "message", "Client profile created successfully",
                        "id", newClient.getId(),
                        "token", token
                )
        );
    }
}
