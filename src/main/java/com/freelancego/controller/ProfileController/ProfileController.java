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

    @PostMapping("/profile/update-section-one-for-freelancer")
    public ResponseEntity<ProfileDto> updateProfileOneSection(@RequestPart(value = "profile") ProfileDto profileDto, @RequestPart(value = "profileImage", required = false) MultipartFile profileFile,@RequestPart(value = "coverPhoto", required = false) MultipartFile coverFile, Authentication auth) throws IOException {
        return ResponseEntity.ok(profileService.updateFreelancerProfileOneSection(profileDto, profileFile, coverFile, auth));
    }

    @PostMapping("/profile/update-section-two-for-freelancer")
    public ResponseEntity<ProfileDto> updateProfileTwoSection(ProfileDto profileDto,Authentication auth){
        return ResponseEntity.ok(profileService.updateFreelancerProfileTwoSection(profileDto,auth.getName()));
    }

    @PostMapping("/profile/update-section-three-for-freelancer")
    public ResponseEntity<ProfileDto> updateProfileThreeSection(@RequestPart(value = "profile") ProfileDto profileDto,@RequestPart(value = "portfolio-section",required = false) MultipartFile portfolioFile,Authentication auth){
        return ResponseEntity.ok(profileService.updateFreelancerProfileThreeSection(profileDto,portfolioFile,auth.getName()));
    }

}
