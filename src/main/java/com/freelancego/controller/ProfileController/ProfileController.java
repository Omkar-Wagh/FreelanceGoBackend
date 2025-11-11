package com.freelancego.controller.ProfileController;

import com.freelancego.dto.user.ProfileDto;
import com.freelancego.service.ProfileService.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/")
public class ProfileController {

    final private ProfileService profileService;

    public ProfileController(ProfileService profileService) {
        this.profileService = profileService;
    }

    @GetMapping("/get-profile/{id}")
    ResponseEntity<ProfileDto> getProfile(@PathVariable("id") int id, Authentication auth){
        return ResponseEntity.ok(profileService.getProfile(id,auth));
    }

    @PostMapping("/profile/{id}/upload-image")
    public ResponseEntity<ProfileDto> uploadImageForUser(@PathVariable("id") int id, @RequestParam("file") MultipartFile image, Authentication auth) throws IOException {
        return ResponseEntity.ok(profileService.uploadProfileImage(id, image, auth));
    }
}
