package com.freelancego.service.ClientService.impl;

import com.freelancego.dto.client.JobDto;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.JobMapper;
import com.freelancego.model.Client;
import com.freelancego.model.Job;
import com.freelancego.model.User;
import com.freelancego.repo.ClientRepository;
import com.freelancego.repo.JobRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ClientService.JobService;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;

@Service
public class JobServiceImpl implements JobService {

    final private UserRepository userRepository;
    final private JobMapper jobMapper;
    final private ClientRepository clientRepository;
    final private JobRepository jobRepository;

    public JobServiceImpl(UserRepository userRepository, JobMapper jobMapper, ClientRepository clientRepository, JobRepository jobRepository) {
        this.userRepository = userRepository;
        this.jobMapper = jobMapper;
        this.clientRepository = clientRepository;
        this.jobRepository = jobRepository;
    }

    public JobDto createPost(JobDto jobDto, String auth) {
        User user = userRepository.findByEmail(auth)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("Client not found"));

        Job job = jobMapper.toEntity(jobDto);
        job.setClient(client);
        // Bids are still null
        jobRepository.save(job);

        return jobMapper.toDto(job);
    }

    public List<JobDto> getPostByClient(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("Client not found"));

        List<Job> jobs = jobRepository.findJobByClient(client);
        List<JobDto> jobDtos = new ArrayList<>();

        for (Job job : jobs) {
            JobDto dto = jobMapper.toDto(job);
            jobDtos.add(dto);
        }

        return jobDtos;
    }


    public JobDto getPostById(int id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("Client not found"));

        Job job = jobRepository.findById(id)
                .orElseThrow(()-> new UserNotFoundException("Job not found"));

        if (job.getClient().getId() != (client.getId())) {
            throw new UnauthorizedAccessException("Unauthorized access to this Job Post");
        }

        return jobMapper.toDto(job);
    }
}
