package com.freelancego.service.UserService;

import com.freelancego.mapper.ClientMapper;
import com.freelancego.mapper.FreelancerMapper;
import com.freelancego.model.*;
import com.freelancego.security.service.JWTService;
import com.freelancego.dto.user.UserDto;
import com.freelancego.enums.Role;
import com.freelancego.exception.BadRequestException;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.UserMapper;
import com.freelancego.repo.ClientRepository;
import com.freelancego.repo.FreelancerRepository;
import com.freelancego.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class UserService {

    final private UserRepository userRepository;
    final private ClientRepository clientRepository;
    final private FreelancerRepository freelancerRepository;
    final private JWTService jwtService;
    final private UserMapper userMapper;
    final private ClientMapper clientMapper;
    final private FreelancerMapper freelancerMapper;

    public UserService(UserRepository userRepository, ClientRepository clientRepository, FreelancerRepository freelancerRepository, JWTService jwtService, UserMapper userMapper, ClientMapper clientMapper, FreelancerMapper freelancerMapper) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.freelancerRepository = freelancerRepository;
        this.jwtService = jwtService;
        this.userMapper = userMapper;
        this.clientMapper = clientMapper;
        this.freelancerMapper = freelancerMapper;
    }

    public UserDto getUserDetails(Authentication auth) {
       User user = userRepository.findByEmail(auth.getName())
               .orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toDTO(user);
    }

    public String updateRole(String role, String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new BadRequestException("Bad Request");
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        String newRoleStr = role.toUpperCase();
        Role newRole;
        try {
            newRole = Role.valueOf(newRoleStr);
        } catch (Exception e) {
            throw new BadRequestException("Invalid Role");
        }

        try {
            List<Role> allowedRoles = List.of(Role.CLIENT, Role.FREELANCER);
            if (!allowedRoles.contains(newRole)) {
                throw new UnauthorizedAccessException("You cannot switch to role: " + newRole);
            }
            user.setRole(newRole);
            userRepository.save(user);
            return jwtService.generateToken(user.getEmail(), newRole.name());
        } catch (Exception e) {
            throw  new InternalServerErrorException("Failed to create token");
        }
    }

    public Map<String, Object> checkRoles(String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Optional<Freelancer> freelancerOpt = freelancerRepository.findByUser(user);
        Optional<Client> clientOpt = clientRepository.findByUser(user);

        Map<String, Object> response = new HashMap<>();
        response.put("user", userMapper.toDTO(user));
        response.put("freelancer", freelancerOpt.map(freelancerMapper::toDTO).orElse(null));
        response.put("client", clientOpt.map(clientMapper::toDTO).orElse(null));

        return response;
    }

    public boolean getIsAuthenticated(Authentication auth) {
        return userRepository.findByEmail(auth.getName()).isPresent();
    }

}
