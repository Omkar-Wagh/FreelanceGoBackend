package com.freelancego.service.ProfileService;


import com.freelancego.dto.user.ProfileDto;
import com.freelancego.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    ProfileDto getProfile(int id, Authentication auth);
    void createProfile(User user);
    ProfileDto updateFreelancerProfileOneSection(ProfileDto profileDto,MultipartFile profileFile, MultipartFile coverFile, Authentication auth) throws IOException;
    ProfileDto updateFreelancerProfileTwoSection(ProfileDto profileDto, String name);
    ProfileDto updateFreelancerProfileThreeSection(ProfileDto profileDto, MultipartFile portfolioFile, String name);
}
