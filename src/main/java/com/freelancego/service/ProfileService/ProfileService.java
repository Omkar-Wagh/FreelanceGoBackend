package com.freelancego.service.ProfileService;


import org.springframework.security.core.Authentication;

import java.util.Map;

public interface ProfileService {
    Map<String, Object> getProfile(int id, Authentication auth);
}
