package com.freelancego.service;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.dto.user.UserDto;
import com.freelancego.enums.Role;
import com.freelancego.model.Client;
import com.freelancego.model.Freelancer;
import com.freelancego.model.User;
import com.freelancego.repo.ClientRepository;
import com.freelancego.repo.FreelancerRepository;
import com.freelancego.repo.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ClientRepository clientRepository;

    @Autowired
    private FreelancerRepository freelancerRepository;

    @Autowired
    private JWTService jwtService;

    public UserDto getUserDetails(Authentication auth) {
       User user = userRepository.findByEmail(auth.getName());
        return new UserDto(user.getId(),user.getUsername(), user.getEmail(),user.getImageData());
    }

    public ResponseEntity<?> uploadProfileImage(int id, MultipartFile image, Authentication auth) throws IOException, IOException {
        User user = userRepository.findById(id).orElseThrow();
        User loggedInUser = userRepository.findByEmail(auth.getName());

        if (user.getId() == loggedInUser.getId()) {
            user.setImageData(image.getBytes());
            userRepository.save(user);
            return ResponseEntity.ok(new UserDto(user.getId(),user.getUsername(), user.getEmail(),user.getImageData()));
        }
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Unauthorized to modify this profile.");
    }

    public ResponseEntity<?> updateRole(Map<String, String> request, String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid Authorization header"));
        }

        String token = authHeader.substring(7);
        String email = jwtService.extractUsername(token);
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "No User Found"));
        }

        String newRoleStr = request.get("role");
        Role newRole;
        try {
            newRole = Role.valueOf(newRoleStr);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Collections.singletonMap("error", "Invalid Role"));
        }
        try {
            user.setRole(newRole);
            userRepository.save(user);
            String newToken = jwtService.generateToken(user.getEmail(), newRole.name());
            return ResponseEntity.ok(Collections.singletonMap("token", newToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Collections.singletonMap("error", "Failed to Generate New Token"));
        }
    }

    public ResponseEntity<?> checkRoles(String username) {

        if (username == null || username.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "Username is required"));
        }

        User user = userRepository.findByEmail(username);
        if (user == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("error", "User not found"));
        }

        Freelancer freelancer = freelancerRepository.findByUser(user);
        Client client = clientRepository.findByUser(user);

        Map<String, Object> response = new HashMap<>();

        response.put("user",new UserDto(user.getId(),user.getUsername(), user.getEmail(),user.getImageData()));
        if (client != null) {
            response.put("client", new ClientDto(client.getId(), client.getCompanyName(),client.getCompanyUrl(),client.getBio(), client.getPhone()));
        } else {
            response.put("client", null);
        }

        if (freelancer != null) {
            String skillsString = freelancer.getSkills();
            List<String> skillsList = Arrays.asList(skillsString.split(","));
            response.put("freelancer", new FreelancerDto(freelancer.getId(), freelancer.getBio(), freelancer.getPortfolioUrl(),skillsList, freelancer.getExperienceLevel().name(), freelancer.getPhone()
            ));
        } else {
            response.put("freelancer", null);
        }

        return ResponseEntity.ok(response);
    }

    public ResponseEntity<?> getIsAuthenticated(Authentication auth) {
        User user = userRepository.findByEmail(auth.getName());
        if (user != null) {
            return ResponseEntity.ok("Authenticated");
//            return ResponseEntity.status(HttpStatus.OK).body(user);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Not Authenticated");
    }


}