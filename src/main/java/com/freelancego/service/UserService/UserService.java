package com.freelancego.service.UserService;

import com.freelancego.mapper.ClientMapper;
import com.freelancego.mapper.FreelancerMapper;
import com.freelancego.security.service.JWTService;
import com.freelancego.dto.client.ClientDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.dto.user.UserDto;
import com.freelancego.enums.Role;
import com.freelancego.exception.BadRequestException;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.UserMapper;
import com.freelancego.model.Client;
import com.freelancego.model.Freelancer;
import com.freelancego.model.User;
import com.freelancego.repo.ClientRepository;
import com.freelancego.repo.FreelancerRepository;
import com.freelancego.repo.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
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

    public UserDto uploadProfileImage(int id, MultipartFile image, Authentication auth) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        User loggedInUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getId() == loggedInUser.getId()) {
            user.setImageData(image.getBytes());
            userRepository.save(user);
            return userMapper.toDTO(user);
        } else {
            throw new UnauthorizedAccessException("Unauthorized to modify this profile.");
        }
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
            user.setRole(newRole);
            userRepository.save(user);
            return jwtService.generateToken(user.getEmail(), newRole.name());
        } catch (Exception e) {
            throw  new InternalServerErrorException("Failed to create token");
        }
    }

    public Map<String,Object> checkRoles(String username) {

        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found"));
        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        Map<String, Object> response = new HashMap<>();

        response.put("user",userMapper.toDTO(user));
        if (client != null) {
            response.put("client",clientMapper.toDTO(client));
        } else {
            response.put("client", null);
        }

        if (freelancer != null) {
            String skillsString = freelancer.getSkills();
            List<String> skillsList = Arrays.asList(skillsString.split(","));
            response.put("freelancer",freelancerMapper.toDTO(freelancer));
        } else {
            response.put("freelancer", null);
        }

        return response;
    }

    public boolean getIsAuthenticated(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
//        userRepository.findByEmail(auth.getName()).isPresent();
        return true;
    }

}
