package com.freelancego.service.FreelancerService.impl;

import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.enums.Role;
import com.freelancego.exception.ConflictException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.FreelancerMapper;
import com.freelancego.model.Freelancer;
import com.freelancego.model.User;
import com.freelancego.repo.FreelancerRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.FreelancerService.FreelancerService;
import com.freelancego.security.service.JWTService;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class FreelancerServiceImpl implements FreelancerService {

    final private UserRepository userRepository;
    final private JWTService jwtService;
    final private FreelancerRepository freelancerRepository;
    final private FreelancerMapper freelancerMapper;

    public FreelancerServiceImpl(UserRepository userRepository, JWTService jwtService, FreelancerRepository freelancerRepository, FreelancerMapper freelancerMapper) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.freelancerRepository = freelancerRepository;
        this.freelancerMapper = freelancerMapper;
    }

    public Map<String,Object> createFreelancer(FreelancerDto freelancerDto, String username) {
        User user = userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        Optional<Freelancer> existingFreelancer = freelancerRepository.findByUser(user);
        if (existingFreelancer.isPresent()) {
            throw new ConflictException("freelancer profile already exists");
        }


        Map<String,Object> response = new HashMap<>();

        Freelancer freelancer = freelancerMapper.toEntity(freelancerDto);
        freelancer.setUser(user);
        freelancerRepository.save(freelancer);
        user.setRole(Role.FREELANCER);
        userRepository.save(user);

        String token = jwtService.generateToken(user.getEmail(),Role.FREELANCER.name());
        response.put("freelancer",freelancerMapper.toDTO(freelancer));
        response.put("token",token);
        return response;
    }

}
