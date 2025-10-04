package com.freelancego.service.FreelancerService.impl;

import com.freelancego.dto.freelancer.BrowseJobDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.enums.JobStatus;
import com.freelancego.enums.Role;
import com.freelancego.exception.ConflictException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.FreelancerMapper;
import com.freelancego.mapper.JobMapper;
import com.freelancego.model.Freelancer;
import com.freelancego.model.Job;
import com.freelancego.model.User;
import com.freelancego.repo.BidRepository;
import com.freelancego.repo.FreelancerRepository;
import com.freelancego.repo.JobRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.FreelancerService.FreelancerService;
import com.freelancego.security.service.JWTService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FreelancerServiceImpl implements FreelancerService {

    final private UserRepository userRepository;
    final private JWTService jwtService;
    final private FreelancerRepository freelancerRepository;
    final private FreelancerMapper freelancerMapper;
    final private JobRepository jobRepository;
    final private BidRepository bidRepository;
    final private JobMapper jobMapper;
    final BrowseJobDto browseJobDto;

    public FreelancerServiceImpl(UserRepository userRepository, JWTService jwtService, FreelancerRepository freelancerRepository, FreelancerMapper freelancerMapper, JobRepository jobRepository, BidRepository bidRepository, JobMapper jobMapper, BrowseJobDto browseJobDto) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.freelancerRepository = freelancerRepository;
        this.freelancerMapper = freelancerMapper;
        this.jobRepository = jobRepository;
        this.bidRepository = bidRepository;
        this.jobMapper = jobMapper;
        this.browseJobDto = browseJobDto;
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

    public List<BrowseJobDto> getBrowseJobs(int page, int size, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("user not found"));

        Pageable pageable = PageRequest.of(page, size);
        List<Job> jobs = jobRepository.findByStatus(pageable, JobStatus.ACTIVE).getContent();

        List<BrowseJobDto> browseJobDtoList = new ArrayList<>();

        for (Job job : jobs) {
            BrowseJobDto dto = new BrowseJobDto();
            jobMapper.toDto(job);
            dto.setAlreadyBid(bidRepository.existsByJobIdAndFreelancerId(job.getId(), user.getId()));
            browseJobDtoList.add(dto);
        }

        return browseJobDtoList;
    }

}
