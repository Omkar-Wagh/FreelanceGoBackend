package com.freelancego.service.ClientService.impl;

import com.freelancego.common.utils.SupabaseUtil;
import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.client.response.DashBoardResponseDto;
import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.dto.user.ContractDto;
import com.freelancego.enums.ContractStatus;
import com.freelancego.enums.JobPhase;
import com.freelancego.enums.JobStatus;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.BidMapper;
import com.freelancego.mapper.ClientMapper;
import com.freelancego.mapper.ContractMapper;
import com.freelancego.mapper.JobMapper;
import com.freelancego.model.*;
import com.freelancego.repo.*;
import com.freelancego.service.ClientService.JobService;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class JobServiceImpl implements JobService {

    final private UserRepository userRepository;
    final private JobMapper jobMapper;
    final private ClientRepository clientRepository;
    final private JobRepository jobRepository;
    final private ContractRepository contractRepository;
    final private ContractMapper contractMapper;
    final private BidMapper bidMapper;
    final private SupabaseUtil supabaseUtil;
    final private BidRepository bidRepository;
    final private ClientMapper clientMapper;

    public JobServiceImpl(UserRepository userRepository, JobMapper jobMapper, ClientRepository clientRepository, JobRepository jobRepository, ContractRepository contractRepository, ContractMapper contractMapper, BidMapper bidMapper, SupabaseUtil supabaseUtil, BidRepository bidRepository, ClientMapper clientMapper) {
        this.userRepository = userRepository;
        this.jobMapper = jobMapper;
        this.clientRepository = clientRepository;
        this.jobRepository = jobRepository;
        this.contractRepository = contractRepository;
        this.contractMapper = contractMapper;
        this.bidMapper = bidMapper;
        this.supabaseUtil = supabaseUtil;
        this.bidRepository = bidRepository;
        this.clientMapper = clientMapper;
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

    public JobDto createPost(JobDto jobDto, MultipartFile file, String auth) {
        User user = userRepository.findByEmail(auth)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("Client not found"));

        Job job = jobMapper.toEntity(jobDto);
        job.setStatus(JobStatus.ACTIVE);
        job.setClient(client);

        String regex = ".*\\.(pdf|png|jpg|jpeg|pptx|docx)$";

        try{
            if (file != null && file.getOriginalFilename().matches(regex)) {
                String attachmentPublicUrl = supabaseUtil.uploadFile(file);
                job.setFile(attachmentPublicUrl);
            }
        }catch (Exception e){
            throw new InternalServerErrorException("Something went wrong while creating the Job " + e.getMessage());
        }

        jobRepository.save(job);

        return jobMapper.toDto(job);
    }

//    public List<JobDto> getPostByClient(int page, int size, String email) {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserNotFoundException("User not found"));
//
//        Client client = clientRepository.findByUser(user)
//                .orElseThrow(() -> new UserNotFoundException("Client not found"));
//
//        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
//        List<Job> jobs = jobRepository.findJobByClient(client, pageable).getContent();
//        List<JobDto> jobDtoList = new ArrayList<>();
//        for(Job job : jobs){
//            int proposalsCount = bidRepository.countBidsByJobId(job.getId());
//            List<String> requiredSkillsList = (job.getRequiredSkills() == null || job.getRequiredSkills().isBlank())
//                    ? List.of()
//                    : Arrays.stream(job.getRequiredSkills().split(","))
//                    .map(String::trim)
//                    .toList();
//            String phase;
//            if(job.getPhase() == null){
//                phase = null;
//            }else{
//                phase = job.getPhase().name();
//            }
//                    JobDto jobDto = new JobDto(
//                    job.getId(),
//                    job.getJobTitle(),
//                    requiredSkillsList,
//                    job.getExperienceLevel().name(),
//                    job.getJobDescription(),
//                    job.getRequirement(),
//                    job.getProjectStartTime(),
//                    job.getProjectEndTime(),
//                    job.getCreatedAt(),
//                    job.getBudget(),
//                    job.getFile(),
//                    job.getStatus().name(),
//                    phase,
//                    clientMapper.toDTO(job.getClient()),
//                    proposalsCount,
//                    false
//            );
//                    jobDtoList.add(jobDto);
//
//        }
//        return jobDtoList;
//    }

    public Page<JobDto> getPostByClient(int page, int size, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Job> jobPage = jobRepository.findJobByClient(client, pageable);

        List<JobDto> jobDtoList = new ArrayList<>();

        for (Job job : jobPage.getContent()) {
            int proposalsCount = bidRepository.countBidsByJobId(job.getId());

            List<String> requiredSkillsList = (job.getRequiredSkills() == null || job.getRequiredSkills().isBlank())
                    ? List.of()
                    : Arrays.stream(job.getRequiredSkills().split(","))
                    .map(String::trim)
                    .toList();

            String phase = (job.getPhase() == null) ? null : job.getPhase().name();

            JobDto jobDto = new JobDto(
                    job.getId(),
                    job.getJobTitle(),
                    requiredSkillsList,
                    job.getExperienceLevel().name(),
                    job.getJobDescription(),
                    job.getRequirement(),
                    job.getProjectStartTime(),
                    job.getProjectEndTime(),
                    job.getCreatedAt(),
                    job.getBudget(),
                    job.getFile(),
                    job.getStatus().name(),
                    phase,
                    clientMapper.toDTO(job.getClient()),
                    proposalsCount,
                    false
            );
            jobDtoList.add(jobDto);
        }

        return new PageImpl<>(jobDtoList, pageable, jobPage.getTotalElements());
    }


    public Map<String, Object> getPostById(int id, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        Job job = jobRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Job not found"));

        JobDto jobDto = jobMapper.toDto(job);

        int proposalsCount = bidRepository.countBidsByJobId(job.getId());

        Map<String,Object> response = new HashMap<>();
        response.put("job", jobDto);
        response.put("proposalsCount", proposalsCount);

        return response;
    }

    public Page<BidDto> getBids(int jobId, int page, int size, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new UserNotFoundException("Job not found"));

        Pageable pageable = PageRequest.of(page,size,Sort.by("submittedAt").descending());
        Page<Bid> bids = bidRepository.findByJob(job,pageable);

        return bids.map(bidMapper::toDto);
    }

    public List<JobDto> getPostByProgress(Client client) {
        List<Job> jobs = jobRepository.findByClientIdAndStatusAndPhaseIn(client.getId(), JobStatus.INACTIVE, Arrays.asList(JobPhase.IN_PROGRESS, JobPhase.PENDING));
        List<JobDto> jobDto = new ArrayList<>();
        return jobMapper.toDtoList(jobs);
    }

    public List<JobDto> getCompletedPost(Client client) {

        List<Job> jobs = jobRepository.findByClientIdAndStatusAndPhase(client.getId(), JobStatus.INACTIVE, JobPhase.SUCCESS);
        List<JobDto> jobDtos = jobMapper.toDtoList(jobs); // returning the job with success
        return jobDtos;
    }

    public List<JobDto> getActivePost(Client client) {
        Pageable pageable = PageRequest.of(0, 3, Sort.by("createdAt").descending());
        List<Job> jobs = jobRepository.findByClient(client,pageable).getContent();
        List<JobDto> jobDtos = jobMapper.toDtoList(jobs);
        return jobDtos;
    }

    public Map<String, Object> getDashboardData(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        List<Contract> completedContracts = contractRepository.findByClientAndStatus(client, ContractStatus.COMPLETED);
        List<Contract> activeContracts = contractRepository.findByClientAndStatus(client, ContractStatus.ACTIVE);

        int totalJobs = completedContracts.size();
        int totalActiveProjects = activeContracts.size();

        double totalSpending = completedContracts.stream()
                .map(Contract::getAcceptedBid)
                .filter(bid -> bid != null)
                .mapToDouble(Bid::getAmount)
                .sum();

        List<JobDto> inProgressPosts = getActivePost(client);

        List<Contract> recentActiveContracts = limitList(activeContracts, 3);
        List<Contract> recentCompletedContracts = limitList(completedContracts, 3);

        DashBoardResponseDto dashboardStats = new DashBoardResponseDto(totalJobs, totalActiveProjects, totalSpending);

        return Map.of(
                "activeProjects", contractMapper.toDtoList(recentActiveContracts),
                "recentJobPosts", inProgressPosts,
                "completedJobs", contractMapper.toDtoList(recentCompletedContracts),
                "dashboard", dashboardStats
        );
    }


    public List<ContractDto> getPostByPhase(String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("Client not found"));

        List<Job> jobs = jobRepository.findByClientIdAndPhase(client.getId(), JobPhase.IN_PROGRESS);
        List<Contract> contractList = new ArrayList<>();

        for (Job job : jobs) {
            Contract contract = contractRepository.findByJob(job);
            contractList.add(contract);
        }
        return contractMapper.toDtoList(contractList);
    }

    public List<JobDto> getPostByStatus(int page, int size, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Job> jobs = jobRepository.findJobByClientAndStatus(client,JobStatus.ACTIVE ,pageable).getContent();

        List<JobDto> jobDtoList = new ArrayList<>();
        for(Job job : jobs){
            int proposalsCount = bidRepository.countBidsByJobId(job.getId());
            List<String> requiredSkillsList = (job.getRequiredSkills() == null || job.getRequiredSkills().isBlank())
                    ? List.of()
                    : Arrays.stream(job.getRequiredSkills().split(","))
                    .map(String::trim)
                    .toList();

            String phase = (job.getPhase() == null) ? null : job.getPhase().name();

            JobDto jobDto = new JobDto(
                    job.getId(),
                    job.getJobTitle(),
                    requiredSkillsList,
                    job.getExperienceLevel().name(),
                    job.getJobDescription(),
                    job.getRequirement(),
                    job.getProjectStartTime(),
                    job.getProjectEndTime(),
                    job.getCreatedAt(),
                    job.getBudget(),
                    job.getFile(),
                    job.getStatus().name(),
                    phase,
                    clientMapper.toDTO(job.getClient()),
                    proposalsCount,
                    false
            );
            jobDtoList.add(jobDto);

        }
        return jobDtoList;
    }

    public Page<ContractDto> getHiredFreelancer(int page, int size, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Contract> contracts = contractRepository.findByClientAndStatus(client, ContractStatus.COMPLETED,pageable);
        return contracts.map(contractMapper::toDTO);
    }

    public Map<String, Object> getAnalytics(String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        List<Job> jobs = jobRepository.findByClient(client);
        List<Contract> contracts = contractRepository.findByClient(client);

        Map<String, Object> response = new HashMap<>();

// 1. Total spent
        double totalSpent = contracts.stream()
                .mapToDouble(c -> c.getAcceptedBid().getAmount())
                .sum();
        response.put("totalSpent", totalSpent);

// 2. Active projects
        long activeProjects = contracts.stream()
                .filter(c -> c.getStatus() == ContractStatus.ACTIVE)
                .count();
        response.put("activeProjects", activeProjects);

// 3. Hired freelancers (distinct)
        long hiredFreelancers = contracts.stream()
                .map(c -> c.getFreelancer().getId())
                .distinct()
                .count();
        response.put("hiredFreelancers", hiredFreelancers);

// 4. Average time to hire (in days)
        double avgTimeToHire = contracts.stream()
                .mapToLong(c -> java.time.Duration.between(c.getJob().getCreatedAt(), c.getAcceptedBid().getSubmittedAt()).toDays())
                .average()
                .orElse(0);
        response.put("avgTimeToHire", avgTimeToHire);

// 5. Category-based project and spending
        Map<String, Double> categorySpending = contracts.stream()
                .collect(Collectors.groupingBy(
                        c -> c.getJob().getCategory(),
                        Collectors.summingDouble(c -> c.getAcceptedBid().getAmount())
                ));
        response.put("categorySpending", categorySpending);

// 6. Proposals per job
        double avgProposalsPerJob = jobs.isEmpty() ? 0 :
                (double) jobs.stream().mapToInt(j -> j.getBids().size()).sum() / jobs.size();
        response.put("avgProposalsPerJob", avgProposalsPerJob);

// 7. Successful hires rate (%)
        double successfulHireRate = jobs.isEmpty() ? 0 :
                (double) contracts.stream().filter(c -> c.getStatus() == ContractStatus.COMPLETED).count() * 100 / jobs.size();
        response.put("successfulHireRate", successfulHireRate);

// 8. Rehire rate (freelancers hired more than once)
        long rehireCount = contracts.stream()
                .collect(Collectors.groupingBy(c -> c.getFreelancer().getId(), Collectors.counting()))
                .values().stream().filter(count -> count > 1).count();
        response.put("rehireRate", rehireCount);

        return response;
    }

}