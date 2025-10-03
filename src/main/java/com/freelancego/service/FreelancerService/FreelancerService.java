package com.freelancego.service.FreelancerService;

import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import org.springframework.http.ResponseEntity;

import java.util.Map;

public interface FreelancerService {

    public Map<String, Object> createFreelancer(FreelancerDto freelancerDto, String username);

}