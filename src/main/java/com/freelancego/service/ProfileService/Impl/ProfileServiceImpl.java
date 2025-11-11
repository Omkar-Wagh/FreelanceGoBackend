package com.freelancego.service.ProfileService.Impl;

import com.freelancego.common.utils.SupabaseUtil;
import com.freelancego.dto.user.PortfolioDetailsDto;
import com.freelancego.dto.user.ProfileDto;
import com.freelancego.exception.BadRequestException;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.ProfileMapper;
import com.freelancego.model.*;
import com.freelancego.repo.ClientRepository;
import com.freelancego.repo.FreelancerRepository;
import com.freelancego.repo.ProfileRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ProfileService.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    final private ProfileRepository profileRepository;
    final private ProfileMapper profileMapper;
    final private UserRepository userRepository;
    final private ClientRepository clientRepository;
    final private FreelancerRepository freelancerRepository;
    final private SupabaseUtil supabaseUtil;

    public ProfileServiceImpl(ProfileRepository profileRepository, ProfileMapper profileMapper, UserRepository userRepository, ClientRepository clientRepository, FreelancerRepository freelancerRepository, SupabaseUtil supabaseUtil) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.freelancerRepository = freelancerRepository;
        this.supabaseUtil = supabaseUtil;
    }

    public void createProfile(User user) {
        Profile profile = profileRepository.findByUser(user).orElse(null);
        Freelancer freelancerOpt = freelancerRepository.findByUser(user).orElse(null);
        Client clientOpt = clientRepository.findByUser(user).orElse(null);

        if (profile == null) {
            profile = new Profile();
            profile.setUser(user);
        }

        profile.setClient(clientOpt);
        profile.setFreelancer(freelancerOpt);
        profileRepository.save(profile);
    }

    public ProfileDto getProfile(int id, Authentication auth) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("user with id "+id+" does not exists")
        );
        User loggedInUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Profile profile = profileRepository.findByUser(user).orElse(null);
        ProfileDto dto = profileMapper.toDto(profile);
        if(loggedInUser.getId() == id){
            dto.setOwnProfile(true);
        }
        return dto;
    }

    public ProfileDto updateFreelancerProfileOneSection(ProfileDto profileDto, MultipartFile profileFile, MultipartFile coverFile, Authentication auth) throws IOException {
        User loggedInUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (profileDto.getId() != loggedInUser.getId()) {
            throw new UnauthorizedAccessException("Unauthorized to modify this profile.");
        }

        Freelancer freelancer = freelancerRepository.findByUser(loggedInUser).orElse(null);
        Profile profile = profileRepository.findByUser(loggedInUser).orElse(null);

        if (profileDto.getUser() != null) {
            loggedInUser.setUsername(profileDto.getUser().username());
            loggedInUser.setPhone(profileDto.getUser().phone());
        }

        if (freelancer != null && profileDto.getFreelancer() != null) {
            freelancer.setDesignation(profileDto.getFreelancer().designation());
            freelancer.setBio(profileDto.getFreelancer().bio());
            freelancer.setPhone(profileDto.getFreelancer().phone());
        }

        if (profile != null && profileDto.getFreelancerProfile() != null) {
            ProfileDetails profileDetails = profile.getFreelancerProfile();
            if (profileDetails == null) {
                profileDetails = new ProfileDetails();
            }

            String bannerUrl = profileDetails.getBannerUrl();

            if (coverFile != null && !coverFile.isEmpty()) {
                String filename = coverFile.getOriginalFilename();
                String regex = "(?i).*\\.(pdf|png|jpg|jpeg|pptx|docx)$";
                if (filename != null && filename.matches(regex)) {
                    try {
                        bannerUrl = supabaseUtil.uploadFile(coverFile);
                    } catch (Exception e) {
                        throw new InternalServerErrorException("Error while uploading cover file: " + e.getMessage());
                    }
                }
            }

            profileDetails.setLocation(profileDto.getFreelancerProfile().getLocation());
            profileDetails.setRating(profileDto.getFreelancerProfile().getRating());
            profileDetails.setBannerUrl(bannerUrl);
            profile.setFreelancerProfile(profileDetails);
        }

        if (profileFile != null && !profileFile.isEmpty()) {
            String contentType = profileFile.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BadRequestException("Invalid file type. Only images are allowed.");
            }
            loggedInUser.setImageData(profileFile.getBytes());
        }

        userRepository.save(loggedInUser);
        if (freelancer != null) freelancerRepository.save(freelancer);
        if (profile != null) profileRepository.save(profile);

        return profileMapper.toDto(profile);
    }

    public ProfileDto updateFreelancerProfileTwoSection(ProfileDto profileDto, String name) {
        User loggedInUser = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (profileDto.getId() != loggedInUser.getId()) {
            throw new UnauthorizedAccessException("Unauthorized to modify this profile.");
        }

        Freelancer freelancer = freelancerRepository.findByUser(loggedInUser).orElse(null);
        Profile profile = profileRepository.findByUser(loggedInUser).orElse(null);

        if (profile != null && profileDto.getFreelancerProfile() != null) {
            ProfileDetails profileDetails = profile.getFreelancerProfile();
            if (profileDetails == null) {
                profileDetails = new ProfileDetails();
            }
            profileDetails.setGithubUrl(profileDto.getFreelancerProfile().getGithubUrl());
            profileDetails.setLinkedinUrl(profileDto.getFreelancerProfile().getLinkedinUrl());
            profile.setFreelancerProfile(profileDetails);
        }

        if (freelancer != null && profileDto.getFreelancer() != null) {
            freelancer.setPortfolioUrl(profileDto.getFreelancer().portfolioUrl());
        }

        if (freelancer != null) freelancerRepository.save(freelancer);
        if (profile != null) profileRepository.save(profile);

        return profileMapper.toDto(profile);
    }

    public ProfileDto updateFreelancerProfileThreeSection(ProfileDto profileDto, MultipartFile portfolioFile, String name) {
        User loggedInUser = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (profileDto.getId() != loggedInUser.getId()) {
            throw new UnauthorizedAccessException("Unauthorized to modify this profile.");
        }

        Profile profile = profileRepository.findByUser(loggedInUser).orElse(null);
        List<PortfolioDetailsDto> portfolioDtos = profileDto.getFreelancerPortfolioDetails();

        if (portfolioDtos != null && !portfolioDtos.isEmpty()) {
            List<PortfolioDetails> portfolioDetailsList = new ArrayList<>();

            for (PortfolioDetailsDto dto : portfolioDtos) {
                PortfolioDetails details = new PortfolioDetails();
                details.setTitle(dto.getTitle());
                details.setDescription(dto.getDescription());
                details.setImageUrl(dto.getImageUrl());
                details.setPortfolioUrl(dto.getPortfolioUrl());

                if (portfolioFile != null && !portfolioFile.isEmpty()) {
                    String filename = portfolioFile.getOriginalFilename();
                    String regex = "(?i).*\\.(pdf|png|jpg|jpeg|pptx|docx)$";
                    if (filename != null && filename.matches(regex)) {
                        try {
                            String uploadedUrl = supabaseUtil.uploadFile(portfolioFile);
                            details.setPortfolioUrl(uploadedUrl);
                        } catch (Exception e) {
                            throw new InternalServerErrorException("Error while uploading portfolio file: " + e.getMessage());
                        }
                    }
                }

                portfolioDetailsList.add(details);
            }

            profile.setFreelancerPortfolioDetails(portfolioDetailsList);
            profileRepository.save(profile);
        }
        return profileMapper.toDto(profile);
    }

}
