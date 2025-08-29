package com.freelancego.security.handler;

import com.freelancego.enums.Role;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.model.User;
import com.freelancego.repo.UserRepository;
import com.freelancego.security.service.JWTService;
import com.freelancego.service.UserService.ImageEncoderService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomOAuth2SuccessHandler implements AuthenticationSuccessHandler {

    final private JWTService jwtService;
    final private UserRepository userRepository;
    final private ImageEncoderService imageEncoder;

    public CustomOAuth2SuccessHandler(JWTService jwtService, UserRepository userRepository, ImageEncoderService imageEncoder) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.imageEncoder = imageEncoder;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        OAuth2AuthenticationToken oauthToken = (OAuth2AuthenticationToken) authentication;
        OAuth2User user = oauthToken.getPrincipal();
        System.out.println(user);

        String email = user.getAttribute("email");
        String name = user.getAttribute("name");
        String picture = user.getAttribute("picture");
        byte [] image = imageEncoder.downloadImageFromUrl(picture);
        // Save user if not exists
        User existingUser = userRepository.findByEmail(email).orElseThrow(() -> new UserNotFoundException("user not found"));
        if (existingUser == null) {
            existingUser = new User();
            existingUser.setEmail(email);
            existingUser.setUsername(name);
            existingUser.setImageData(image);
            existingUser.setRole(Role.ROLE_USER_PENDING);
            userRepository.save(existingUser);
        }
        // Generate JWT token
        String role = existingUser.getRole().name();
        String token = jwtService.generateToken(email,role);
        response.sendRedirect("http://localhost:5173/profile-setup?token=" + token);
    }
}
