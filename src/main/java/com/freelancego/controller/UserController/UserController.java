package com.freelancego.controller.UserController;

import com.freelancego.dto.user.UserDto;
import com.freelancego.service.UserService.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    final private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication auth) {
        return ResponseEntity.ok(userService.getUserDetails(auth));
    }

    @GetMapping("/isAuthenticated")
    public ResponseEntity<Boolean> getAuthentication(Authentication auth) {
        boolean result = userService.getIsAuthenticated(auth);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/update-role")
    @PreAuthorize("hasAnyRole('CLIENT', 'FREELANCER')")
    public ResponseEntity<String> updateRole(@RequestParam String role, @RequestHeader("Authorization") String authHeader) {
        return ResponseEntity.ok(userService.updateRole(role,authHeader));
    }

    @GetMapping("/check-role")
    public ResponseEntity<Map<String,Object>> checkAvailableRole(Authentication auth) {
        return ResponseEntity.ok(userService.checkRoles(auth.getName()));
    }

    @GetMapping("/wakeup")
    public ResponseEntity<String> wakeup() {
        return ResponseEntity.ok().build();
    }
}
