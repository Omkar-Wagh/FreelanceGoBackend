package com.freelancego.service.ContractService.Impl;

import com.freelancego.dto.user.ContractDto;
import com.freelancego.enums.BidStatus;
import com.freelancego.enums.ContractStatus;
import com.freelancego.enums.JobPhase;
import com.freelancego.exception.InvalidIdException;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.ContractMapper;
import com.freelancego.model.*;
import com.freelancego.repo.*;
import com.freelancego.service.ContractService.ContractService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ContractServiceImpl implements ContractService {

    private final UserRepository userRepository;
    private final ClientRepository clientRepository;
    private final FreelancerRepository freelancerRepository;
    private final JobRepository jobRepository;
    private final ContractRepository contractRepository;
    private final ContractMapper contractMapper;
    private final BidRepository bidRepository;

    public ContractServiceImpl(UserRepository userRepository, ClientRepository clientRepository, FreelancerRepository freelancerRepository, JobRepository jobRepository, ContractRepository contractRepository, ContractMapper contractMapper, BidRepository bidRepository) {
        this.userRepository = userRepository;
        this.clientRepository = clientRepository;
        this.freelancerRepository = freelancerRepository;
        this.jobRepository = jobRepository;
        this.contractRepository = contractRepository;
        this.contractMapper = contractMapper;
        this.bidRepository = bidRepository;
    }


    public void getContract(int jobId, int freelancerId, int clientId, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if(jobId <= 0){
            throw new InvalidIdException("Invalid job Id " + jobId);
        }
        if(clientId <= 0){
            throw new InvalidIdException("Invalid client Id " + clientId);
        }
        if(freelancerId <= 0){
            throw new InvalidIdException("Invalid freelancer Id " + freelancerId);
        }

        Job job = jobRepository.findById(jobId).orElseThrow(
                () -> new UserNotFoundException("Job not found for Id " + jobId));

        Client client = clientRepository.findById(clientId).orElseThrow(
                ()-> new UserNotFoundException("Client not found for Id " + clientId));

        Freelancer freelancer = freelancerRepository.findById(freelancerId).orElseThrow(
                ()-> new UserNotFoundException("Freelancer not found with Id " + freelancerId));

        Contract contract = contractRepository.findByJobAndClientAndFreelancer(job,client,freelancer);

        contractMapper.toDTO(contract);
    }

    public ContractDto createContract(int bidId, int jobId, int freelancerId, int clientId, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId)
                .orElseThrow(() -> new UserNotFoundException("Job not found"));
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));
        Freelancer freelancer = freelancerRepository.findById(freelancerId)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found"));
        Bid bid = bidRepository.findById(bidId)
                .orElseThrow(() -> new UserNotFoundException("Bid not found"));

        if (user.getId() == job.getClient().getUser().getId()) {
            throw new UnauthorizedAccessException("Only the client can create a contract for this job.");
        }

        if (contractRepository.existsByJobId(jobId)) {
            throw new InvalidIdException("A contract already exists for this job.");
        }

        Contract contract = new Contract();
        contract.setJob(job);
        contract.setClient(client);
        contract.setFreelancer(freelancer);
        contract.setAcceptedBid(bid);
        contract.setStatus(ContractStatus.ACTIVE);

        job.setPhase(JobPhase.IN_PROGRESS);

        job.getBids().forEach(currentBid -> {
            if (currentBid.getId() != bid.getId()) {
                currentBid.setStatus(BidStatus.REJECTED);
            }
        });
        bid.setStatus(BidStatus.ACCEPTED);
        bid.setEditable(false);

        contractRepository.save(contract);

        return contractMapper.toDTO(contract);
    }


}
