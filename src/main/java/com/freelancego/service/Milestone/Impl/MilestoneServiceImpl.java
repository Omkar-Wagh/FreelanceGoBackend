package com.freelancego.service.Milestone.Impl;

import com.freelancego.common.utils.SupabaseUtil;
import com.freelancego.dto.user.MilestoneDto;
import com.freelancego.dto.user.MilestonePaymentResponse;
import com.freelancego.dto.user.SubmissionDto;
import com.freelancego.enums.*;
import com.freelancego.exception.BadRequestException;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.listeners.types.MilestoneEvent;
import com.freelancego.mapper.MilestoneMapper;
import com.freelancego.mapper.SubmissionMapper;
import com.freelancego.model.*;
import com.freelancego.repo.*;
import com.freelancego.service.Milestone.MilestoneService;
import com.freelancego.service.payment.PaymentService;
import jakarta.transaction.Transactional;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    final private SupabaseUtil supabaseUtil;
    final private PaymentService paymentService;
    final private ApplicationEventPublisher applicationEventPublisher;

    public MilestoneServiceImpl(UserRepository userRepository, ContractRepository contractRepository, MilestoneRepository milestoneRepository, MilestoneMapper milestoneMapper, SubmissionRepository submissionRepository, SubmissionMapper submissionMapper, ClientRepository clientRepository, FreelancerRepository freelancerRepository, SupabaseUtil supabaseUtil, PaymentService paymentService, ApplicationEventPublisher applicationEventPublisher) {
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.milestoneRepository = milestoneRepository;
        this.milestoneMapper = milestoneMapper;
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
        this.clientRepository = clientRepository;
        this.freelancerRepository = freelancerRepository;
        this.supabaseUtil = supabaseUtil;
        this.paymentService = paymentService;
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public List<MilestoneDto> getMileStone(int contractId, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new RuntimeException("Contract not found"));

        int freelancerId = contract.getFreelancer().getId();
        int clientId = contract.getClient().getId();

        // Contract must belong to logged in user
        if (user.getId() != freelancerId && user.getId() != clientId) {
            throw new UnauthorizedAccessException("You are not authorised to view milestones");
        }

        List<Milestone> milestones = milestoneRepository.findByContract(contract);

        if (user.getRole().equals(Role.FREELANCER)) {
            return milestoneMapper.toDtoList(milestones);
        }

        boolean lastMilestoneCreated = milestones.stream().anyMatch(Milestone::isLast);

        if (!lastMilestoneCreated) {
            return Collections.emptyList();
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
            throw new IllegalArgumentException("Invalid milestone number. Expected: " + nextAllowedSequence);
        }

        milestone.setContract(contract);
        milestone.setClientFeedback(null);
        milestone.setLocked(milestoneDto.isLast());

        if (milestoneDto.getSubmission() != null) {
            Submission submission = submissionMapper.toEntity(milestoneDto.getSubmission());
            milestone.setSubmission(submission);
            submissionRepository.save(submission);
        }


        Milestone saved = milestoneRepository.save(milestone);

        // * Publishing the event to NotificationListener to create a notification
        applicationEventPublisher.publishEvent(
                new MilestoneEvent(
                        contract.getFreelancer().getUser(),
                        contract.getClient().getUser(),
                        NotificationType.MILESTONE_CREATED
                )
        );

        return milestoneMapper.toDTO(saved);
    }

    public List<MilestoneDto> updateMilestone(List<MilestoneDto> milestoneDtos, int freelancerId, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found"));

        if (freelancer.getId() != freelancerId) {
            throw new UnauthorizedAccessException(
                    "You are not authorised to update milestones");
        }

        if (milestoneDtos == null || milestoneDtos.isEmpty()) {
            return Collections.emptyList();
        }

        // fetch first milestone to get contract (since all belong to same contract)
        Milestone firstMilestone = milestoneRepository.findById(milestoneDtos.get(0).getId())
                .orElseThrow(() -> new UserNotFoundException("Milestone not found"));

        Contract contract = firstMilestone.getContract();

        // validate freelancer owns contract
        if (contract.getFreelancer().getId() != freelancerId) {
            throw new UnauthorizedAccessException(
                    "Contract does not belong to this freelancer");
        }

        List<Milestone> updatedMilestones = new ArrayList<>();

        for (MilestoneDto dto : milestoneDtos) {

            Milestone milestone = milestoneRepository.findById(dto.getId())
                    .orElseThrow(() -> new UserNotFoundException(
                            "Milestone not found with id " + dto.getId()));

            // ensure same contract (safety check)
            if (milestone.getContract().getId() != contract.getId()) {
                throw new BadRequestException(
                        "All milestones must belong to the same contract");
            }

            if (milestone.isLocked()) {
                throw new BadRequestException(
                        "Milestone " + milestone.getId() + " is locked");
            }

            milestone.setTitle(dto.getTitle());
            milestone.setDescription(dto.getDescription());
            milestone.setAmount(dto.getAmount());
            milestone.setDueDate(dto.getDueDate());

            updatedMilestones.add(milestone);
        }

        contract.setVerificationStatus(VerificationStatus.PENDING_REVIEW);

        milestoneRepository.saveAll(updatedMilestones);
        contractRepository.save(contract);

        return milestoneMapper.toDtoList(updatedMilestones);
    }

    public List<MilestoneDto> editMilestone(List<MilestoneDto> milestoneDtos, int clientId, String name) {

        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        if (client.getId() != clientId) {
            throw new UnauthorizedAccessException(
                    "You are not authorised to edit milestones");
        }

        if (milestoneDtos == null || milestoneDtos.isEmpty()) {
            return Collections.emptyList();
        }

        // Fetch first milestone to resolve contract
        Milestone firstMilestone = milestoneRepository.findById(milestoneDtos.get(0).getId())
                .orElseThrow(() -> new UserNotFoundException(
                        "Milestone not found"));

        Contract contract = firstMilestone.getContract();

        // validate ownership once
        if (contract.getClient().getId() != clientId) {
            throw new UnauthorizedAccessException(
                    "This contract does not belong to the client");
        }

        // block if already approved
        if (contract.getVerificationStatus() == VerificationStatus.APPROVED_BY_CLIENT) {
            throw new BadRequestException(
                    "Contract already approved, cannot modify milestones");
        }

        List<Milestone> updatedMilestones = new ArrayList<>();

        for (MilestoneDto dto : milestoneDtos) {

            Milestone milestone = milestoneRepository.findById(dto.getId())
                    .orElseThrow(() -> new UserNotFoundException(
                            "Milestone not found with id " + dto.getId()));

            // safety check: same contract
            if (milestone.getContract().getId() != contract.getId()) {
                throw new BadRequestException(
                        "All milestones must belong to the same contract");
            }

            milestone.setClientFeedback(dto.getClientFeedback());

            updatedMilestones.add(milestone);
        }

        milestoneRepository.saveAll(updatedMilestones);

        return milestoneMapper.toDtoList(updatedMilestones);
    }

    @Transactional
    public MilestonePaymentResponse approveMilestone(int milestoneId, int clientId, String email) {

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        if (client.getId() != clientId) {
            throw new UnauthorizedAccessException("Not authorized");
        }

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new UserNotFoundException("Milestone not found"));

//        contr.setVerificationStatus(VerificationStatus.APPROVED_BY_CLIENT);
//        milestoneRepository.save(milestone);

        return paymentService.createPaymentOrder(milestone);
    }

    public SubmissionDto getSubmission(int milestoneId, int clientId, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("client not found"));

        if(clientId != client.getId()){
            throw new UnauthorizedAccessException("your are not authorised to view submission");
        }
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(()-> new UserNotFoundException("milestone not found"));
        Submission submission = null;
        if(milestone != null){
            submission = milestone.getSubmission();
        }
        return submissionMapper.toDTO(submission);
    }

    public SubmissionDto createSubmission(SubmissionDto submissionDto, MultipartFile file,int milestoneId, int freelancerId, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("freelancer not found"));

        if (freelancerId != freelancer.getId()) {
            throw new UnauthorizedAccessException("your are not authorised to create submission");
        }
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new UserNotFoundException("milestone not found"));

        if (milestone.getContract().getFreelancer().getId() != freelancer.getId()) {
            throw new BadRequestException("unauthorised to perform submission operation");
        }
        Submission submission = submissionMapper.toEntity(submissionDto);

        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();
            String regex = "(?i).*\\.(pdf|png|jpg|jpeg|pptx|docx)$";
            if (filename != null && filename.matches(regex)) {
                try {
                    fileUrl = supabaseUtil.uploadFile(file);
                } catch (Exception e) {
                    throw new InternalServerErrorException("Error while uploading file: " + e.getMessage());
                }
            }
        }
        submission.setFileUrl(fileUrl);
        submission.setStatus(SubmissionStatus.PENDING_REVIEW);
        submissionRepository.save(submission);
        milestone.setStatus(MilestoneStatus.SUBMITTED);
        milestone.setSubmission(submission);
        milestoneRepository.save(milestone);
        return submissionMapper.toDTO(submission);
    }

    public SubmissionDto updateSubmission(SubmissionDto submissionDto, MultipartFile file, int milestoneId, int freelancerId, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("freelancer not found"));

        if (freelancerId != freelancer.getId()) {
            throw new UnauthorizedAccessException("your are not authorised to create submission");
        }
        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(() -> new UserNotFoundException("milestone not found"));

        if (milestone.getContract().getFreelancer().getId() != freelancer.getId()) {
            throw new BadRequestException("unauthorised to perform submission operation");
        }
        Submission submission = submissionRepository.findById(submissionDto.getId()).orElse(null);

        String fileUrl = null;
        if (file != null && !file.isEmpty()) {
            String filename = file.getOriginalFilename();
            String regex = "(?i).*\\.(pdf|png|jpg|jpeg|pptx|docx)$";
            if (filename != null && filename.matches(regex)) {
                try {
                    fileUrl = supabaseUtil.uploadFile(file);
                } catch (Exception e) {
                    throw new InternalServerErrorException("Error while uploading file: " + e.getMessage());
                }
            }
        }

        if(submission != null && submission.getStatus() != SubmissionStatus.APPROVED){
            submission.setNotes(submissionDto.getNotes());
            submission.setFileUrl(fileUrl);
            submission.setStatus(SubmissionStatus.PENDING_REVIEW);
            submissionRepository.save(submission);
            milestone.setStatus(MilestoneStatus.SUBMITTED);
            milestone.setSubmission(submission);
            milestoneRepository.save(milestone);
        }
        return submissionMapper.toDTO(submission);
    }

    public SubmissionDto editSubmission(SubmissionDto submissionDto, int clientId, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("client not found"));

        if(clientId != client.getId()){
            throw new UnauthorizedAccessException("your are not authorised to provide remark");
        }

        Submission submission = submissionRepository.findById(submissionDto.getId())
                .orElseThrow(() -> new UserNotFoundException("submission not found"));

        Milestone milestone = milestoneRepository.findById(submission.getId())
                .orElseThrow(()-> new UserNotFoundException("milestone not found"));

        if (milestone.getContract().getClient().getId() != client.getId()) {
            throw new BadRequestException("unauthorised to provide submission remark");
        }

        if(submissionDto != null && submission.getStatus() != SubmissionStatus.APPROVED){
            submission.setClientRemark(submissionDto.getClientRemark());
            submission.setStatus(SubmissionStatus.REVISION_REQUESTED);
            submissionRepository.save(submission);
            milestone.setSubmission(submission);
            milestone.setStatus(MilestoneStatus.REVISION_REQUESTED);
            milestoneRepository.save(milestone);
        }

        return submissionMapper.toDTO(submission);
    }

    @Transactional
    public MilestoneDto approveSubmission(int submissionId, int clientId, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("client not found"));

        if(clientId != client.getId()){
            throw new UnauthorizedAccessException("your are not authorised to approve submission");
        }

        Submission submission = submissionRepository.findById(submissionId)
                .orElseThrow(() -> new UserNotFoundException("submission not found"));

        Milestone milestone = milestoneRepository.findBySubmission(submission);


        if (milestone.getContract().getClient().getId() != client.getId()) {
            throw new BadRequestException("unauthorised to provide submission approval");
        }

        submission.setStatus(SubmissionStatus.APPROVED);

        if(submission != null) submissionRepository.save(submission);
        milestone.setSubmission(submission);
        milestone.setStatus(MilestoneStatus.SUBMITTED);

        if(milestone != null) milestoneRepository.save(milestone);

        try{
            paymentService.releaseMilestonePayment(milestone);
        }catch (Exception e){
            throw new RuntimeException(" Failed to make payout " + e.getMessage());
        }
        return milestoneMapper.toDTO(milestone);
    }

    public String approveMilestoneSequence(int contractId, int clientId, String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Client not found"));

        Contract contract = contractRepository.findById(contractId)
                .orElseThrow(() -> new UserNotFoundException("Contract not found"));

        if (client.getId() != clientId && contract.getClient().getId() != clientId) {
            throw new UnauthorizedAccessException("Not authorized");
        }

        List<Milestone> milestones = milestoneRepository.findByContract(contract);
        milestones.forEach(m -> m.setLocked(true));
        milestoneRepository.saveAll(milestones);

        contract.setVerificationStatus(VerificationStatus.APPROVED_BY_CLIENT);
        contractRepository.save(contract);
        return "contract milestones Approved";
    }
}