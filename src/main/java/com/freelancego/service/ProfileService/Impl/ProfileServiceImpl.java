package com.freelancego.service.ProfileService.Impl;

import com.freelancego.common.utils.SupabaseUtil;
import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.user.CertificationDetailsDto;
import com.freelancego.dto.user.PortfolioDetailsDto;
import com.freelancego.dto.user.ProfileDto;
import com.freelancego.enums.ContractStatus;
import com.freelancego.exception.*;
import com.freelancego.mapper.ClientMapper;
import com.freelancego.mapper.ProfileMapper;
import com.freelancego.model.*;
import com.freelancego.repo.*;
import com.freelancego.service.ProfileService.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ProfileServiceImpl implements ProfileService {

    final private ProfileRepository profileRepository;
    final private ProfileMapper profileMapper;
    final private UserRepository userRepository;
    final private ClientRepository clientRepository;
    final private FreelancerRepository freelancerRepository;
    final private SupabaseUtil supabaseUtil;
    final private ContractRepository contractRepository;
    final private ClientMapper clientMapper;
    final private BidRepository bidRepository;

    public ProfileServiceImpl(ProfileRepository profileRepository, ProfileMapper profileMapper, UserRepository userRepository,
                              ClientRepository clientRepository, FreelancerRepository freelancerRepository, SupabaseUtil supabaseUtil,
                              ContractRepository contractRepository, ClientMapper clientMapper, BidRepository bidRepository) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.freelancerRepository = freelancerRepository;
        this.supabaseUtil = supabaseUtil;
        this.contractRepository = contractRepository;
        this.clientMapper = clientMapper;
        this.bidRepository = bidRepository;
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
        if(loggedInUser.getId() == id && dto != null){
            dto.setOwnProfile(true);
        }
        return dto;
    }

    public ProfileDto updateFreelancerProfileOneSection(ProfileDto profileDto, MultipartFile profileFile,
                                                        MultipartFile coverFile, Authentication auth) {
        User loggedInUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (profileDto == null) {
            throw new InvalidIdException("profile dto is empty, could perform any operation");
        }

        if (profileDto.getUser().id() != loggedInUser.getId()) {
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

        try{
            if (profileFile != null && !profileFile.isEmpty()) {
                String contentType = profileFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new BadRequestException("Invalid file type. Only images are allowed.");
                }
                loggedInUser.setImageData(profileFile.getBytes());
            }
        }catch (Exception e){
            throw new InternalServerErrorException("something went wrong while saving profile image.");
        }

        userRepository.save(loggedInUser);
        if (freelancer != null) freelancerRepository.save(freelancer);
        if (profile != null) profileRepository.save(profile);

        ProfileDto dto = profileMapper.toDto(profile);

        if(profileDto.getUser().id() == loggedInUser.getId() && dto != null){
            dto.setOwnProfile(true);
        }
        return dto;
    }

    public ProfileDto updateFreelancerProfileTwoSection(ProfileDto profileDto, String name) {
        User loggedInUser = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (profileDto == null) {
            throw new InvalidIdException("profile dto is empty, could perform any operation");
        }

        if (profileDto.getUser().id() != loggedInUser.getId()) {
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

        ProfileDto dto = profileMapper.toDto(profile);
        if(profileDto.getUser().id() == loggedInUser.getId() && dto != null){
            dto.setOwnProfile(true);
        }
        return dto;
    }

    public ProfileDto updateFreelancerProfileThreeSection(ProfileDto profileDto,MultipartFile imageFile,
                                                          MultipartFile portfolioFile, String name) {
        User loggedInUser = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (profileDto == null) {
            throw new InvalidIdException("profile dto is empty, could perform any operation");
        }

        if (profileDto.getUser().id() != loggedInUser.getId()) {
            throw new UnauthorizedAccessException("Unauthorized to modify this profile.");
        }

        Profile profile = profileRepository.findByUser(loggedInUser).orElse(null);
        List<PortfolioDetailsDto> portfolioDtos = profileDto.getFreelancerPortfolioDetails();

        if (portfolioDtos != null && !portfolioDtos.isEmpty() && profile != null) {
            List<PortfolioDetails> portfolioDetailsList = profile.getFreelancerPortfolioDetails();

            if(portfolioDetailsList == null){
                portfolioDetailsList = new ArrayList<>();
            }

            for (PortfolioDetailsDto dto : portfolioDtos) {
                PortfolioDetails details = new PortfolioDetails();
                details.setTitle(dto.getTitle());
                details.setDescription(dto.getDescription());

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

                if (imageFile != null && !imageFile.isEmpty()) {
                    String filename = imageFile.getOriginalFilename();
                    String regex = "(?i).*\\.(pdf|png|jpg|jpeg|pptx|docx)$";
                    if (filename != null && filename.matches(regex)) {
                        try {
                            String imageUrl = supabaseUtil.uploadFile(imageFile);
                            details.setImageUrl(imageUrl);
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
        ProfileDto dto = profileMapper.toDto(profile);
        if(profileDto.getUser().id() == loggedInUser.getId() && dto != null){
            dto.setOwnProfile(true);
        }
        return dto;
    }

    public ProfileDto updateFreelancerProfileFourSection(ProfileDto profileDto, MultipartFile certificationFile,
                                                         String name) {
        User loggedInUser = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (profileDto == null) {
            throw new InvalidIdException("profile dto is empty, could perform any operation");
        }

        if (profileDto.getUser().id() != loggedInUser.getId()) {
            throw new UnauthorizedAccessException("Unauthorized to modify this profile.");
        }

        Profile profile = profileRepository.findByUser(loggedInUser)
                .orElseThrow(() -> new UserNotFoundException("Profile not found"));

        List<CertificationDetailsDto> certificationDtos = profileDto.getFreelancerCertificationDetails();

        if (certificationDtos != null && !certificationDtos.isEmpty()) {
            List<CertificationsDetails> existingCertifications = profile.getFreelancerCertificationDetails();

            if (existingCertifications == null) {
                existingCertifications = new ArrayList<>();
            }

            for (CertificationDetailsDto dto : certificationDtos) {
                CertificationsDetails details = new CertificationsDetails();
                details.setCertificateName(dto.getCertificateName());
                details.setProvider(dto.getProvider());
                details.setCertificateUrl(dto.getCertificateUrl());

                if (certificationFile != null && !certificationFile.isEmpty()) {
                    String filename = certificationFile.getOriginalFilename();
                    String regex = "(?i).*\\.(pdf|png|jpg|jpeg|docx)$";
                    if (filename != null && filename.matches(regex)) {
                        try {
                            String uploadedUrl = supabaseUtil.uploadFile(certificationFile);
                            details.setCertificateUrl(uploadedUrl);
                        } catch (Exception e) {
                            throw new InternalServerErrorException("Error uploading certification file: " + e.getMessage());
                        }
                    }
                }

                existingCertifications.add(details);
            }

            profile.setFreelancerCertificationDetails(existingCertifications);
            profileRepository.save(profile);
        }
        ProfileDto dto = profileMapper.toDto(profile);
        if(profileDto.getUser().id() == loggedInUser.getId() && dto != null){
            dto.setOwnProfile(true);
        }
        return dto;
    }

    public ProfileDto updateClientProfileOneSection(ProfileDto profileDto, MultipartFile profileFile,
                                                    MultipartFile coverFile, Authentication auth) {
        User loggedInUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (profileDto == null) {
            throw new InvalidIdException("profile dto is empty, could perform any operation");
        }

        if (profileDto.getUser().id() != loggedInUser.getId()) {
            throw new UnauthorizedAccessException("Unauthorized to modify this profile.");
        }

        Client client = clientRepository.findByUser(loggedInUser).orElse(null);
        Profile profile = profileRepository.findByUser(loggedInUser).orElse(null);

        if (profileDto.getUser() != null) {
            loggedInUser.setUsername(profileDto.getUser().username());
            loggedInUser.setPhone(profileDto.getUser().phone());
        }

        if (client != null && profileDto.getFreelancer() != null) {
            client.setCompanyName(profileDto.getClient().companyName());
            client.setBio(profileDto.getClient().bio());
            client.setPhone(profileDto.getClient().phone());
        }

        if (profile != null && profileDto.getClientProfile() != null) {
            ProfileDetails profileDetails = profile.getClientProfile();
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
            profile.setClientProfile(profileDetails);
        }

        try{
            if (profileFile != null && !profileFile.isEmpty()) {
                String contentType = profileFile.getContentType();
                if (contentType == null || !contentType.startsWith("image/")) {
                    throw new BadRequestException("Invalid file type. Only images are allowed.");
                }
                loggedInUser.setImageData(profileFile.getBytes());
            }
        }catch (Exception e){
            throw new InternalServerErrorException("something went wrong while saving profile image.");
        }

        userRepository.save(loggedInUser);
        if (client != null) clientRepository.save(client);
        if (profile != null) profileRepository.save(profile);

        ProfileDto dto = profileMapper.toDto(profile);
        if(profileDto.getUser().id() == loggedInUser.getId() && dto != null){
            dto.setOwnProfile(true);
        }
        return dto;
    }

    public ProfileDto updateClientProfileTwoSection(ProfileDto profileDto, String name) {
        User loggedInUser = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (profileDto == null) {
            throw new InvalidIdException("profile dto is empty, could perform any operation");
        }

        if (profileDto.getUser().id() != loggedInUser.getId()) {
            throw new UnauthorizedAccessException("Unauthorized to modify this profile.");
        }

        Client client = clientRepository.findByUser(loggedInUser).orElse(null);
        Profile profile = profileRepository.findByUser(loggedInUser).orElse(null);

        if (profile != null && profileDto.getClientProfile() != null) {
            ProfileDetails profileDetails = profile.getClientProfile();
            if (profileDetails == null) {
                profileDetails = new ProfileDetails();
            }
            profileDetails.setGithubUrl(profileDto.getClientProfile().getGithubUrl());
            profileDetails.setLinkedinUrl(profileDto.getClientProfile().getLinkedinUrl());
            profile.setClientProfile(profileDetails);
        }

        if (client != null && profileDto.getClientProfile() != null) {
            client.setCompanyUrl(profileDto.getClient().companyUrl());
        }

        if (client != null) clientRepository.save(client);
        if (profile != null) profileRepository.save(profile);

        ProfileDto dto = profileMapper.toDto(profile);
        if(profileDto.getUser().id() == loggedInUser.getId() && dto != null){
            dto.setOwnProfile(true);
        }
        return dto;
    }

    public List<JobDto> getClientProfileThreeSection(int id,String name) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new UserNotFoundException("user not found"));

        Client client = clientRepository.findByUser(user).orElseThrow(
                ()-> new UserNotFoundException("client not found"));
        List<Contract> contracts = contractRepository.findByClientAndStatus(client, ContractStatus.ACTIVE);
        List<JobDto> jobDtoList = new ArrayList<>();
        for(Contract contract : contracts){
            Job job = contract.getJob();
            int proposalsCount = bidRepository.countBidsByJobId(job.getId());
            List<String> requiredSkillsList = (job.getRequiredSkills() == null || job.getRequiredSkills().isBlank())
                    ? List.of()
                    : Arrays.stream(job.getRequiredSkills().split(","))
                    .map(String::trim)
                    .toList();

            String phase = (job.getPhase() == null) ? null : job.getPhase().name();

            JobDto jobDto = new JobDto(
                    job.getId(),
                    job.getJobTitle(),
                    requiredSkillsList,
                    job.getExperienceLevel().name(),
                    job.getJobDescription(),
                    job.getRequirement(),
                    job.getProjectStartTime(),
                    job.getProjectEndTime(),
                    job.getCreatedAt(),
                    job.getBudget(),
                    job.getFile(),
                    job.getStatus().name(),
                    phase,
                    clientMapper.toDTO(job.getClient()),
                    proposalsCount,
                    false
            );
            jobDtoList.add(jobDto);
        }
        return jobDtoList;
    }

}
