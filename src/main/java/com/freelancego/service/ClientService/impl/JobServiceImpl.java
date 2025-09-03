package com.freelancego.service.ClientService.impl;

import com.freelancego.dto.client.JobDto;
import com.freelancego.enums.JobPhase;
import com.freelancego.enums.JobStatus;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.JobMapper;
import com.freelancego.model.*;
import com.freelancego.repo.ClientRepository;
import com.freelancego.repo.ContractRepository;
import com.freelancego.repo.JobRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.ClientService.JobService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class JobServiceImpl implements JobService {

    final private UserRepository userRepository;
    final private JobMapper jobMapper;
    final private ClientRepository clientRepository;
    final private JobRepository jobRepository;
    final private ContractRepository contractRepository;

    public JobServiceImpl(UserRepository userRepository, JobMapper jobMapper, ClientRepository clientRepository, JobRepository jobRepository,ContractRepository contractRepository) {
        this.userRepository = userRepository;
        this.jobMapper = jobMapper;
        this.clientRepository = clientRepository;
        this.jobRepository = jobRepository;
        this.contractRepository = contractRepository;
    }

    public <T> List<T> limitList(List<T> list, int limit) {
        return list.stream()
                .limit(limit)
                .toList();
    }

    public Map<String, Object> getUserAndClientByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("client", client);

        return result;
    }

    public JobDto createPost(JobDto jobDto, String auth) {
        User user = userRepository.findByEmail(auth)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("Client not found"));

        Job job = jobMapper.toEntity(jobDto);
        job.setCreatedAt(OffsetDateTime.now());
        job.setStatus(JobStatus.ACTIVE);
        job.setClient(client);
        // Bids Phase are still null
        jobRepository.save(job);

        return jobMapper.toDto(job);
    }

    public Page<JobDto> getPostByClient(int page, int size, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Job> jobs = jobRepository.findJobByClient(client, pageable);

        return jobs.map(jobMapper::toDto);
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

    public List<JobDto> getPostByProgress(Client client) {
        List<Job> jobs = jobRepository.findByClientIdAndStatusAndPhaseIn(client.getId(), JobStatus.INACTIVE, Arrays.asList(JobPhase.IN_PROGRESS, JobPhase.PENDING));
        List<JobDto> jobDto = new ArrayList<>();
        return jobMapper.toDtoList(jobs);
    }

    public List<JobDto> getCompletedPost(Client client) {

        List<Job> jobs = jobRepository.findByClientIdAndStatusAndPhaseIn(client.getId(), JobStatus.INACTIVE, JobPhase.SUCCESS);
        List<JobDto> jobDtos = jobMapper.toDtoList(jobs); // returning the job with success
        return jobDtos;
    }

    public List<JobDto> getActivePost(Client client) {
        List<Job> jobs = jobRepository.findByClientIdAndStatusAndPhaseIn(client.getId(), JobStatus.ACTIVE);
        List<JobDto> jobDtos = jobMapper.toDtoList(jobs);
        return jobDtos;
    }


    public Map<String, Object> getDashboardData(String name) {
        Map<String, Object> result = getUserAndClientByEmail(name);
        User user = (User) result.get("user");
        Client client = (Client) result.get("client");

        List<JobDto> activePosts = getActivePost(client);
        List<JobDto> inProgressPosts = getPostByProgress(client);
        List<JobDto> completedPosts = getCompletedPost(client);

        int totalJobs = completedPosts.size();
        int totalActiveProjects = inProgressPosts.size();

        double totalPending = 0;
        for (JobDto job : completedPosts) {
            Contract contract = contractRepository.findById(job.id())
                    .orElseThrow(() -> new UserNotFoundException("Contract not found for job " + job.id()));

            Bid bid = contract.getAcceptedBid();
            if (bid != null) {
                totalPending += bid.getAmount();
            }
        }

        List<JobDto> activePosts1 = limitList(getActivePost(client), 3);
        List<JobDto> inProgressPosts1 = limitList(getPostByProgress(client),3);
        List<JobDto> completedPosts1 = limitList(getCompletedPost(client),3);

        return Map.of(
                "active", activePosts1,
                "progress", inProgressPosts1,
                "complete", completedPosts1,
                "totalJobs", totalJobs,
                "totalActiveProjects", totalActiveProjects,
                "totalPending", totalPending
        );
    }

}
