package com.freelancego.service.FreelancerService;

import com.freelancego.dto.freelancer.FreelancerDto;
import java.util.Map;

public interface FreelancerService {

    public Map<String, Object> createFreelancer(FreelancerDto freelancerDto, String username);

}