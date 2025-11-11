package com.freelancego.service.ProfileService;


import com.freelancego.dto.user.ProfileDto;
import com.freelancego.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ProfileService {
    ProfileDto getProfile(int id, Authentication auth);
    ProfileDto uploadProfileImage(int id, MultipartFile image, Authentication auth) throws IOException;

    void createProfile(User user);
}
