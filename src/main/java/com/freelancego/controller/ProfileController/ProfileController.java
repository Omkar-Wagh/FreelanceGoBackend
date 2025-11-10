package com.freelancego.controller.ProfileController;

import com.freelancego.service.ProfileService.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/")
public class ProfileController {

    final private ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/get-profile/{id}")
    ResponseEntity<Map<String,Object>> getProfile(@PathVariable("id") int id, Authentication auth){
        return ResponseEntity.ok(profileService.getProfile(id,auth));
    }
}
