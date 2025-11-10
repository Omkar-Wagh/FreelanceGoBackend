package com.freelancego.service.ProfileService.Impl;

import com.freelancego.dto.user.ProfileDto;
import com.freelancego.mapper.ProfileMapper;
import com.freelancego.model.Profile;
import com.freelancego.repo.ProfileRepository;
import com.freelancego.service.ProfileService.ProfileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ProfileServiceImpl implements ProfileService {

    @Autowired
    private ProfileRepository profileRepository;

    @Autowired
    private ProfileMapper profileMapper;

//    public ProfileDto getProfileByUserId(int userId) {
//        Profile profile = profileRepository.findByUserId(userId)
//                .orElseThrow(() -> new RuntimeException("Profile not found"));
//        return profileMapper.toDto(profile);
//    }

    public Profile updateProfile(ProfileDto dto) {
        Profile entity = profileMapper.toEntity(dto);
        return profileRepository.save(entity);
    }

    public Map<String, Object> getProfile(int id, Authentication auth) {
        return Map.of();
    }
}
