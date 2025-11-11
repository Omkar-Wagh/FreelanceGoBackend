package com.freelancego.service.ProfileService.Impl;

import com.freelancego.dto.user.ProfileDto;
import com.freelancego.exception.BadRequestException;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.ProfileMapper;
import com.freelancego.mapper.UserMapper;
import com.freelancego.model.Client;
import com.freelancego.model.Profile;
import com.freelancego.model.User;
import com.freelancego.repo.ProfileRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ProfileService.ProfileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@Service
public class ProfileServiceImpl implements ProfileService {

    final private ProfileRepository profileRepository;
    final private ProfileMapper profileMapper;
    final private UserRepository userRepository;
    final private UserMapper userMapper;

    public ProfileServiceImpl(ProfileRepository profileRepository, ProfileMapper profileMapper, UserRepository userRepository, UserMapper userMapper) {
        this.profileRepository = profileRepository;
        this.profileMapper = profileMapper;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }


    public Profile updateProfile(ProfileDto dto) {
        Profile entity = profileMapper.toEntity(dto);
        return profileRepository.save(entity);
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

    public ProfileDto uploadProfileImage(int id, MultipartFile image, Authentication auth) throws IOException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        User loggedInUser = userRepository.findByEmail(auth.getName())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (user.getId() == loggedInUser.getId()) {
            if (image == null || image.isEmpty()) {
                throw new BadRequestException("No image file provided");
            }
            user.setImageData(image.getBytes());
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new BadRequestException("Invalid file type. Only images are allowed");
            }
            userRepository.save(user);
//            return userMapper.toDTO(user);
            return null;
        } else {
            throw new UnauthorizedAccessException("Unauthorized to modify this profile.");
        }
    }

    public void createProfile(User user) {
    }
}
//profileService.createProfile(user);