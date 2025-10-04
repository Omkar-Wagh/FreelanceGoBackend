package com.freelancego.service.FreelancerService;

import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.dto.freelancer.BrowseJobDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Map;

public interface FreelancerService {

    public Map<String, Object> createFreelancer(FreelancerDto freelancerDto, String username);

    List<BrowseJobDto> getBrowseJobs(int page, int size, String name);
}