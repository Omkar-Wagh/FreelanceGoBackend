package com.freelancego.service.FreelancerService;

import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.enums.ExperienceLevel;
import com.freelancego.enums.Role;
import com.freelancego.model.Freelancer;
import com.freelancego.model.User;
import com.freelancego.repo.FreelancerRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.UserService.JWTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class FreelancerService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private FreelancerRepository freelancerRepository;

    public ResponseEntity<?> createFreelancer(FreelancerDto freelancerDto, String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        }

        Freelancer existingFreelancer = freelancerRepository.findByUser(user);
        if (existingFreelancer != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("error", "Freelancer profile already exists"));
        }

        ExperienceLevel experienceLevel;
        try {
            experienceLevel = ExperienceLevel.valueOf(freelancerDto.experienceLevel().toUpperCase());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("error", "Invalid experience level"));
        }

        String skillsString = String.join(",", freelancerDto.skills());

        Freelancer newFreelancer = new Freelancer();
        newFreelancer.setUser(user);
        newFreelancer.setDesignation(freelancerDto.designation());
        newFreelancer.setBio(freelancerDto.bio());
        newFreelancer.setPhone(freelancerDto.phone());
        newFreelancer.setPortfolioUrl(freelancerDto.portfolioUrl());
        newFreelancer.setSkills(skillsString);
        newFreelancer.setExperienceLevel(experienceLevel);
        freelancerRepository.save(newFreelancer);
        user.setRole(Role.FREELANCER);
        userRepository.save(user);
        String token = jwtService.generateToken(user.getEmail(),Role.FREELANCER.name());

        return ResponseEntity.ok(
                Map.of(
                        "message", "Freelancer profile created successfully",
                        "id", newFreelancer.getId(),
                        "token", token
                )
        );
    }

}
