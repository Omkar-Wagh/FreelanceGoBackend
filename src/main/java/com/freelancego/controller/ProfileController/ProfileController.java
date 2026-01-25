package com.freelancego.controller.ProfileController;

import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.user.CertificationDto;
import com.freelancego.dto.user.PortfolioDto;
import com.freelancego.dto.user.ProfileDto;
import com.freelancego.service.ProfileService.ProfileService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PostMapping("/profile/update-freelancer-profile")
    public ResponseEntity<ProfileDto> updateProfileOneSection(@RequestPart(value = "profile") ProfileDto profileDto,
                                                              @RequestPart(value = "profileImage", required = false) MultipartFile profileFile,
                                                              @RequestPart(value = "coverPhoto", required = false) MultipartFile coverFile,
                                                              Authentication auth) {
        return ResponseEntity.ok(profileService.updateFreelancerProfileOneSection(profileDto, profileFile, coverFile, auth));
    }

    @PostMapping("/profile/update-freelancer-social-links")
    public ResponseEntity<ProfileDto> updateProfileTwoSection(@RequestBody ProfileDto profileDto,Authentication auth){
        return ResponseEntity.ok(profileService.updateFreelancerProfileTwoSection(profileDto,auth.getName()));
    }

    @PostMapping("/profile/create-freelancer-portfolio")
    public ResponseEntity<ProfileDto> createProfileThreeSection(@RequestPart(value = "portfolio") PortfolioDto portfolioDto,
                                                                @RequestPart(value = "portfolio-image",required = false) MultipartFile imageFile,
                                                                Authentication auth){
        return ResponseEntity.ok(profileService.createFreelancerProfileThreeSection(portfolioDto,imageFile,auth.getName()));
    }

    @PostMapping("/profile/update-freelancer-portfolio")
    public ResponseEntity<ProfileDto> updateProfileThreeSection(@RequestPart(value = "portfolio") PortfolioDto portfolioDto,
                                                                @RequestPart(value = "portfolio-image",required = false) MultipartFile imageFile,
                                                                Authentication auth){
        return ResponseEntity.ok(profileService.updateFreelancerProfileThreeSection(portfolioDto,imageFile,auth.getName()));
    }

    @PostMapping("/profile/create-freelancer-certification")
    public ResponseEntity<ProfileDto> createProfileFourSection(@RequestPart(value = "certificate") CertificationDto dto,
                                                               @RequestPart(value = "certification-image",required = false) MultipartFile certificationFile,
                                                               Authentication auth){
        return ResponseEntity.ok(profileService.createFreelancerProfileFourSection(dto,certificationFile,auth.getName()));
    }

    @PostMapping("/profile/update-freelancer-certification")
    public ResponseEntity<ProfileDto> updateProfileFourSection(@RequestPart(value = "certificate") CertificationDto dto,
                                                               @RequestPart(value = "certification-image",required = false) MultipartFile certificationFile,
                                                               Authentication auth){
        return ResponseEntity.ok(profileService.updateFreelancerProfileFourSection(dto,certificationFile,auth.getName()));
    }

    @PostMapping("/profile/update-client-profile")
    public ResponseEntity<ProfileDto> updateClientProfileOneSection(@RequestPart(value = "profile") ProfileDto profileDto,
                                                                    @RequestPart(value = "profileImage", required = false) MultipartFile profileFile,
                                                                    @RequestPart(value = "coverPhoto", required = false) MultipartFile coverFile,
                                                                    Authentication auth){
        return ResponseEntity.ok(profileService.updateClientProfileOneSection(profileDto, profileFile, coverFile, auth));
    }

    @PostMapping("/profile/update-client-social-links")
    public ResponseEntity<ProfileDto> updateClientProfileTwoSection(@RequestBody ProfileDto profileDto,Authentication auth){
        return ResponseEntity.ok(profileService.updateClientProfileTwoSection(profileDto,auth.getName()));
    }

    @GetMapping("/profile/get-client-analytics/{userId}")
    public ResponseEntity<List<JobDto>> updateClientProfileThreeSection(@PathVariable("userId")int id, Authentication auth){
        return ResponseEntity.ok(profileService.getClientProfileThreeSection(id,auth.getName()));
    }

}
