package com.freelancego.service.FreelancerService.impl;

import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.dto.freelancer.BrowseJobDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.dto.user.ContractDto;
import com.freelancego.enums.ContractStatus;
import com.freelancego.enums.JobStatus;
import com.freelancego.enums.Role;
import com.freelancego.exception.ConflictException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.BidMapper;
import com.freelancego.mapper.ContractMapper;
import com.freelancego.mapper.FreelancerMapper;
import com.freelancego.mapper.JobMapper;
import com.freelancego.model.*;
import com.freelancego.repo.*;
import com.freelancego.service.FreelancerService.FreelancerService;
import com.freelancego.security.service.JWTService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class FreelancerServiceImpl implements FreelancerService {

    final private UserRepository userRepository;
    final private JWTService jwtService;
    final private FreelancerRepository freelancerRepository;
    final private FreelancerMapper freelancerMapper;
    final private JobRepository jobRepository;
    final private BidRepository bidRepository;
    final private JobMapper jobMapper;
    final private ContractRepository contractRepository;
    final private ContractMapper contractMapper;
    final private BidMapper bidMapper;

    public FreelancerServiceImpl(UserRepository userRepository, JWTService jwtService, FreelancerRepository freelancerRepository, FreelancerMapper freelancerMapper, JobRepository jobRepository, BidRepository bidRepository, JobMapper jobMapper, ContractRepository contractRepository, ContractMapper contractMapper, BidMapper bidMapper) {
        this.userRepository = userRepository;
        this.jwtService = jwtService;
        this.freelancerRepository = freelancerRepository;
        this.freelancerMapper = freelancerMapper;
        this.jobRepository = jobRepository;
        this.bidRepository = bidRepository;
        this.jobMapper = jobMapper;
        this.contractRepository = contractRepository;
        this.contractMapper = contractMapper;
        this.bidMapper = bidMapper;
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
        Freelancer freelancer = freelancerRepository.findByUser(user).orElseThrow(
                ()-> new UserNotFoundException("freelancer not found")
        );

        Pageable pageable = PageRequest.of(page, size);
        List<Job> jobs = jobRepository.findByStatus(pageable, JobStatus.ACTIVE).getContent();

        List<BrowseJobDto> browseJobDtoList = new ArrayList<>();

        for (Job job : jobs) {
            BrowseJobDto dto = new BrowseJobDto();

            dto.setJob(jobMapper.toDto(job));
            boolean alreadyBid = bidRepository.existsByJobIdAndFreelancerId(job.getId(), freelancer.getId());
            dto.setAlreadyBid(alreadyBid);

            if (job.getClient().getUser().getId() == user.getId()) {
                dto.setOwnPost(true);
            }

            browseJobDtoList.add(dto);
        }

        return browseJobDtoList;
    }

    public List<JobDto> getPostByStatus(int page, int size, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        List<Job> jobs = jobRepository.findJobByStatus(JobStatus.ACTIVE, pageable).getContent();

        List<Job> filteredJobs = jobs.stream()
                .filter(job -> job.getBids() != null &&
                        job.getBids().stream()
                                .anyMatch(bid -> bid.getFreelancer().getId() == freelancer.getId()))
                .toList();
        long totalBids = jobRepository.countByActiveBids(JobStatus.ACTIVE, freelancer.getId());

        Map<String, Object> response = new HashMap<>();
        response.put("totalBids", totalBids);
        response.put("submittedProposals",filteredJobs.stream().map(jobMapper::toDto).toList());
//        return response;
        return filteredJobs.stream().map(jobMapper::toDto).toList();
    }

    public List<ContractDto> getPostByPhase(String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found"));

        List<Contract> activeContracts = contractRepository.findByFreelancerAndStatus(
                freelancer, ContractStatus.ACTIVE
        );

        return contractMapper.toDtoList(activeContracts);
    }

    public Map<String, Object> getBidHistory(int page, int size, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found"));

        // Compute full stats + get all jobs where this freelancer has bids
        Map<String, Object> globalStats = calculateBidStatsForFreelancer(freelancer);
        List<Job> freelancerJobs = (List<Job>) globalStats.get("FreelancerJobs");

        // Paginate only over freelancer’s bid jobs
        int start = Math.min(page * size, freelancerJobs.size());
        int end = Math.min(start + size, freelancerJobs.size());
        List<Job> pagedJobs = freelancerJobs.subList(start, end);

        // Prepare job-with-bid data
        List<Map<String, Object>> jobWithBids = pagedJobs.stream()
                .map(job -> {
                    Map<String, Object> jobMap = new HashMap<>();
                    jobMap.put("job", jobMapper.toDto(job));
                    List<BidDto> bids = job.getBids().stream().map(bidMapper::toDto).toList();
                    if (!bids.isEmpty()) jobMap.put("myBid", bids.get(0));
                    return jobMap;
                })
                .toList();

        // Final response: global stats + paginated results
        Map<String, Object> response = new HashMap<>(globalStats);
        response.put("Current Page", page);
        response.put("Page Size", size);
        response.put("Total Pages", (int) Math.ceil((double) freelancerJobs.size() / size));
        response.put("JobWithBid", jobWithBids);
        response.put("Jobs", pagedJobs.stream().map(jobMapper::toDto).toList());

        return response;
    }


    public Map<String, Object> calculateBidStatsForFreelancer(Freelancer freelancer) {
        List<Job> allJobs = jobRepository.findAll();

        List<Job> freelancerJobs = allJobs.stream()
                .filter(job -> job.getBids() != null &&
                        job.getBids().stream()
                                .anyMatch(bid -> (bid.getFreelancer().getId() == (freelancer.getId()))))
                .peek(job -> {
                    List<Bid> myBidOnly = job.getBids().stream()
                            .filter(b -> (b.getFreelancer().getId() == (freelancer.getId())))
                            .toList();
                    job.setBids(myBidOnly);
                })
                .toList();

        long totalJobs = freelancerJobs.size();
        long totalProposals = freelancerJobs.stream().flatMap(j -> j.getBids().stream()).count();
        List<Bid> myBids = freelancerJobs.stream().flatMap(j -> j.getBids().stream()).toList();

        List<Contract> contracts = myBids.stream()
                .map(contractRepository::findByAcceptedBid)
                .filter(Objects::nonNull)
                .toList();

        long hired = contracts.size();
        long inReview = contracts.stream().filter(c -> c.getStatus() == ContractStatus.ACTIVE).count();
        long completed = contracts.stream().filter(c -> c.getStatus() == ContractStatus.COMPLETED).count();

        Map<String, Object> stats = new HashMap<>();
        stats.put("Total Jobs", totalJobs);
        stats.put("Total Proposals", totalProposals);
        stats.put("Hired", hired);
        stats.put("In Review", inReview);
        stats.put("Completed", completed);
        stats.put("FreelancerJobs", freelancerJobs); // temporarily store for reuse
        return stats;
    }



    public Map<String, Object> getEarningsDashboard(int page, int size, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found"));

        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Contract> contractPage = contractRepository.findByFreelancer(freelancer, pageable);
        List<Contract> contracts = contractPage.getContent();

        List<Contract> allContracts = contractRepository.findByFreelancer(freelancer);

        double totalEarnings = allContracts.stream()
                .filter(c -> c.getStatus() == ContractStatus.COMPLETED)
                .mapToLong(c -> c.getAcceptedBid().getAmount())
                .sum();

        double inEscrow = allContracts.stream()
                .filter(c -> c.getStatus() == ContractStatus.ACTIVE)
                .mapToLong(c -> c.getAcceptedBid().getAmount())
                .sum();

        double avgEarningsPerProject = allContracts.stream()
                .filter(c -> c.getStatus() == ContractStatus.COMPLETED)
                .mapToLong(c -> c.getAcceptedBid().getAmount())
                .average()
                .orElse(0);

        Map<String, Object> earningsDashboard = new HashMap<>();
        earningsDashboard.put("totalEarnings", totalEarnings);
        earningsDashboard.put("inEscrow", inEscrow);
        earningsDashboard.put("avgEarningsPerProject", avgEarningsPerProject);
        earningsDashboard.put("recentContracts", contractMapper.toDtoList(contracts));
        earningsDashboard.put("currentPage", contractPage.getNumber());
        earningsDashboard.put("totalPages", contractPage.getTotalPages());
        earningsDashboard.put("totalContracts", contractPage.getTotalElements());

        return earningsDashboard;
    }

    public Map<String, Object> getAnalytics(String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("user not found"));
        Freelancer freelancer = freelancerRepository.findByUser(user).orElseThrow(
                ()-> new UserNotFoundException("freelancer not found"));

        List<Bid> bids = bidRepository.findByFreelancer(freelancer);
        List<Contract> contracts = contractRepository.findByFreelancer(freelancer);

        int totalBids = bids == null ? 0 : bids.size();
        int totalContracts = contracts == null ? 0 : contracts.size();

        double totalEarnings = contracts == null ? 0.0 :
                contracts.stream()
                        .filter(c -> c.getAcceptedBid() != null)
                        .mapToLong(c -> {
                            Long amt = c.getAcceptedBid().getAmount();
                            return amt == null ? 0 : amt;
                        })
                        .sum();

        long activeProjects = contracts == null ? 0 :
                contracts.stream().filter(c -> c.getStatus() == ContractStatus.ACTIVE).count();

        long completedProjects = contracts == null ? 0 :
                contracts.stream().filter(c -> c.getStatus() == ContractStatus.COMPLETED).count();

        double avgTimeToComplete = contracts == null ? 0.0 :
                contracts.stream()
                        .filter(c -> c.getStatus() == ContractStatus.COMPLETED)
                        .mapToLong(c -> {
                            OffsetDateTime start = c.getCreateAt();
                            OffsetDateTime end = null;
                            if (c.getJob() != null && c.getJob().getProjectEndTime() != null) {
                                end = c.getJob().getProjectEndTime();
                            }
                            if (end == null && c.getAcceptedBid() != null) {
                                end = c.getAcceptedBid().getSubmittedAt();
                            }
                            if (start == null || end == null) return -1L;
                            return java.time.Duration.between(start, end).toDays();
                        })
                        .filter(days -> days >= 0)
                        .average()
                        .orElse(0.0);

        double avgBidAmount = totalBids == 0 ? 0.0 :
                bids.stream()
                        .map(Bid::getAmount)
                        .filter(Objects::nonNull)
                        .mapToLong(Long::longValue)
                        .average()
                        .orElse(0);

        double winRatePercent = totalBids == 0 ? 0.0 :
                ((double) totalContracts * 100.0) / totalBids;

        int proposalsSubmitted = totalBids;

        Map<Integer, Long> contractsByClient = contracts == null ? Map.of() :
                contracts.stream()
                        .filter(c -> c.getClient() != null)
                        .collect(Collectors.groupingBy(c -> c.getClient().getId(), Collectors.counting()));

        long clientsRehired = contractsByClient.values().stream().filter(cnt -> cnt > 1).count();
        long distinctClients = contractsByClient.size();
        double rehireRatePercent = distinctClients == 0 ? 0.0 :
                ((double) clientsRehired * 100.0) / distinctClients;

        Map<String, Double> categoryEarnings = contracts == null ? Map.of() :
                contracts.stream()
                        .filter(c -> c.getJob() != null && c.getAcceptedBid() != null)
                        .collect(Collectors.groupingBy(
                                c -> Optional.ofNullable(c.getJob().getCategory()).orElse("Unspecified"),
                                Collectors.summingDouble(c -> Optional.ofNullable(c.getAcceptedBid().getAmount()).orElse(0l))
                        ));

        Map<String, Long> jobsWonPerCategory = contracts == null ? Map.of() :
                contracts.stream()
                        .filter(c -> c.getJob() != null)
                        .collect(Collectors.groupingBy(
                                c -> Optional.ofNullable(c.getJob().getCategory()).orElse("Unspecified"),
                                Collectors.counting()
                        ));

        Map<String, Object> response = new LinkedHashMap<>();
        response.put("totalEarnings", totalEarnings);
        response.put("activeProjects", activeProjects);
        response.put("completedProjects", completedProjects);
        response.put("avgTimeToCompleteDays", avgTimeToComplete);
        response.put("avgBidAmount", avgBidAmount);
        response.put("winRatePercent", winRatePercent);
        response.put("proposalsSubmitted", proposalsSubmitted);
        response.put("rehireRatePercent", rehireRatePercent);
        response.put("categoryEarnings", categoryEarnings);
        response.put("jobsWonPerCategory", jobsWonPerCategory);

        return response;
    }

}
