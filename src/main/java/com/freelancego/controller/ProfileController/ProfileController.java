package com.freelancego.controller.ProfileController;

import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.user.ProfileDto;
import com.freelancego.service.ProfileService.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

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
    public ResponseEntity<ProfileDto> updateProfileOneSection(@RequestPart(value = "profile") ProfileDto profileDto,
                                                              @RequestPart(value = "profileImage", required = false) MultipartFile profileFile,
                                                              @RequestPart(value = "coverPhoto", required = false) MultipartFile coverFile,
                                                              Authentication auth) {
        return ResponseEntity.ok(profileService.updateFreelancerProfileOneSection(profileDto, profileFile, coverFile, auth));
    }

    @PostMapping("/profile/update-section-two-for-freelancer")
    public ResponseEntity<ProfileDto> updateProfileTwoSection(ProfileDto profileDto,Authentication auth){
        return ResponseEntity.ok(profileService.updateFreelancerProfileTwoSection(profileDto,auth.getName()));
    }

    @PostMapping("/profile/update-section-three-for-freelancer")
    public ResponseEntity<ProfileDto> updateProfileThreeSection(@RequestPart(value = "profile") ProfileDto profileDto,
                                                                @RequestPart(value = "portfolio-section",required = false) MultipartFile imageFile,
                                                                @RequestPart(value = "portfolio-section",required = false) MultipartFile portfolioFile,
                                                                Authentication auth){
        return ResponseEntity.ok(profileService.updateFreelancerProfileThreeSection(profileDto,imageFile,portfolioFile,auth.getName()));
    }

    @PostMapping("/profile/update-section-four-for-freelancer")
    public ResponseEntity<ProfileDto> updateProfileFourSection(@RequestPart(value = "profile") ProfileDto profileDto,
                                                               @RequestPart(value = "certification-section",required = false) MultipartFile certificationFile,
                                                               Authentication auth){
        return ResponseEntity.ok(profileService.updateFreelancerProfileFourSection(profileDto,certificationFile,auth.getName()));
    }

    @PostMapping("/profile/update-section-one-for-client")
    public ResponseEntity<ProfileDto> updateClientProfileOneSection(@RequestPart(value = "profile") ProfileDto profileDto,
                                                                    @RequestPart(value = "profileImage", required = false) MultipartFile profileFile,
                                                                    @RequestPart(value = "coverPhoto", required = false) MultipartFile coverFile,
                                                                    Authentication auth){
        return ResponseEntity.ok(profileService.updateClientProfileOneSection(profileDto, profileFile, coverFile, auth));
    }

    @PostMapping("/profile/update-section-two-for-client")
    public ResponseEntity<ProfileDto> updateClientProfileTwoSection(ProfileDto profileDto,Authentication auth){
        return ResponseEntity.ok(profileService.updateClientProfileTwoSection(profileDto,auth.getName()));
    }

    @GetMapping("/profile/update-section-three-for-client")
    public ResponseEntity<List<JobDto>> updateClientProfileThreeSection(Authentication auth){
        return ResponseEntity.ok(profileService.updateClientProfileThreeSection(auth.getName()));
    }

}
