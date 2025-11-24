package com.freelancego.service.MilestoneService.Impl;

import com.freelancego.dto.user.MilestoneDto;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.MilestoneMapper;
import com.freelancego.mapper.SubmissionMapper;
import com.freelancego.model.*;
import com.freelancego.repo.*;
import com.freelancego.service.MilestoneService.MilestoneService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MilestoneServiceImpl implements MilestoneService {

    final private UserRepository userRepository;
    final private ContractRepository contractRepository;
    final private MilestoneRepository milestoneRepository;
    final private MilestoneMapper milestoneMapper;
    final private SubmissionRepository submissionRepository;
    final private SubmissionMapper submissionMapper;
    final private ClientRepository clientRepository;
    final private FreelancerRepository freelancerRepository;

    public MilestoneServiceImpl(UserRepository userRepository, ContractRepository contractRepository, MilestoneRepository milestoneRepository, MilestoneMapper milestoneMapper, SubmissionRepository submissionRepository, SubmissionMapper submissionMapper, ClientRepository clientRepository, FreelancerRepository freelancerRepository) {
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.milestoneRepository = milestoneRepository;
        this.milestoneMapper = milestoneMapper;
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
        this.clientRepository = clientRepository;
        this.freelancerRepository = freelancerRepository;
    }

    public List<MilestoneDto> getMileStone(int contractId,String name) {
        User user = userRepository.findByEmail(name).orElseThrow(
                () -> new UserNotFoundException("user not found"));
        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(()->new UserNotFoundException("contract not found for id "+contractId));

        List<Milestone> milestones = milestoneRepository.findByContract(contract);
        int freelancerId = contract.getFreelancer().getId();
        int clientId = contract.getClient().getId();
        if(user.getId() != freelancerId && user.getId() != clientId){
            throw new UnauthorizedAccessException("your are not authorised to view milestone");
        }
        return milestoneMapper.toDtoList(milestones);
    }

    public MilestoneDto createMilestone(MilestoneDto milestoneDto,int clientId,int freelancerId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("freelancer not found"));

        if(freelancerId != freelancer.getId()){
            throw new UnauthorizedAccessException("your are not authorised to create the milestone");
        }

        Contract contract = contractRepository.findById(milestoneDto.getContract().id())
                .orElseThrow(() -> new UserNotFoundException("Contract not found"));

        if(contract.getFreelancer().getId() != user.getId()){
            throw new UnauthorizedAccessException("your are not authorised to create the milestone");
        }

        Milestone milestone = milestoneMapper.toEntity(milestoneDto);

        List<Milestone> milestones = milestoneRepository.findByContract(contract);

        int nextAllowedSequence = milestones.isEmpty()
                ? 1
                : milestones.stream()
                .mapToInt(Milestone::getMilestoneNumber)
                .max()
                .orElse(0) + 1;

        if (milestone.getMilestoneNumber() != nextAllowedSequence) {
            throw new IllegalArgumentException(
                    "Invalid milestone number. Expected: " + nextAllowedSequence
            );
        }

        milestone.setContract(contract);

        if (milestoneDto.getSubmission() != null) {
            Submission submission = submissionMapper.toEntity(milestoneDto.getSubmission());
            milestone.setSubmission(submission);
            submissionRepository.save(submission);
        }

        Milestone saved = milestoneRepository.save(milestone);
        return milestoneMapper.toDTO(saved);
    }

}
