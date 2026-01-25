package com.freelancego.service.ProfileService;


import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.user.CertificationDto;
import com.freelancego.dto.user.PortfolioDto;
import com.freelancego.dto.user.ProfileDto;
import com.freelancego.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface ProfileService {
    ProfileDto getProfile(int id, Authentication auth);
    void createProfile(User user);
    ProfileDto updateFreelancerProfileOneSection(ProfileDto profileDto,MultipartFile profileFile, MultipartFile coverFile, Authentication auth);
    ProfileDto updateFreelancerProfileTwoSection(ProfileDto profileDto, String name);
    ProfileDto updateFreelancerProfileThreeSection(PortfolioDto portfolioDto, MultipartFile imageFile, String name);
    ProfileDto updateFreelancerProfileFourSection(CertificationDto dto, MultipartFile certificationFile, String name);

    ProfileDto updateClientProfileOneSection(ProfileDto profileDto, MultipartFile profileFile, MultipartFile coverFile, Authentication auth);
    ProfileDto updateClientProfileTwoSection(ProfileDto profileDto, String name);
    List<JobDto> getClientProfileThreeSection(int id,String name);

    ProfileDto createFreelancerProfileThreeSection(PortfolioDto portfolioDto, MultipartFile imageFile, String name);

    ProfileDto createFreelancerProfileFourSection(CertificationDto dto, MultipartFile certificationFile, String name);

    String deleteFreelancerProfileThreeSection(int id, String name);

    String deleteFreelancerProfileFourSection(int id, String name);
}
