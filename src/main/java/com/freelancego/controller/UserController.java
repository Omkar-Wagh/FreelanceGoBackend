package com.freelancego.controller;

import com.freelancego.dto.user.UserDto;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.UserService.JWTService;
import com.freelancego.service.UserService.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private JWTService jwtService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication auth) {
        return ResponseEntity.ok(userService.getUserDetails(auth));
    }

    @GetMapping("/isAuthenticated")
    public ResponseEntity<?> getAuthentication(Authentication auth) {
        return userService.getIsAuthenticated(auth);
    }

    @PostMapping("/profile/{id}/upload-image")
    public ResponseEntity<?> uploadImageForUser(@PathVariable("id") int id, @RequestParam("file") MultipartFile image, Authentication auth) throws IOException {
        return userService.uploadProfileImage(id, image, auth);
    }

    @PostMapping("/update-role")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<?> updateRole(@RequestParam String role, @RequestHeader("Authorization") String authHeader) {
        return userService.updateRole(role,authHeader);
    }

    @GetMapping("/check-role")
    public ResponseEntity<?> checkAvailableRole(Authentication auth) {
        return userService.checkRoles(auth.getName());
    }
}
