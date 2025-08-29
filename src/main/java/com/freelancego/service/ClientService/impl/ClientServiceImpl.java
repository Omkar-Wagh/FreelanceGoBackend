package com.freelancego.service.ClientService.impl;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.enums.Role;
import com.freelancego.exception.ConflictException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.ClientMapper;
import com.freelancego.model.Client;
import com.freelancego.model.User;
import com.freelancego.repo.ClientRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ClientService.ClientService;
import com.freelancego.security.service.JWTService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class ClientServiceImpl implements ClientService {

    final private UserRepository userRepository;
    final private ClientRepository clientRepository;
    final private JWTService jwtService;
    final private ClientMapper clientMapper;

    public ClientServiceImpl(UserRepository userRepository, ClientRepository clientRepository, JWTService jwtService, ClientMapper clientMapper) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.jwtService = jwtService;
        this.clientMapper = clientMapper;
    }

    public Map<String,Object> createClient(ClientDto clientDto, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<Client> existingClientOpt = clientRepository.findByUser(user);
        if (existingClientOpt.isPresent()) {
            throw new ConflictException("Client profile already exists");
        }

        Map<String,Object> response = new HashMap<>();
        Client client = clientMapper.toEntity(clientDto);
        clientRepository.save(client);

        user.setRole(Role.CLIENT);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(),Role.CLIENT.name());
        response.put("client",clientMapper.toDTO(client));
        response.put("token",token);

        return response;
    }

}