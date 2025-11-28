package com.freelancego.service.MilestoneService.Impl;

import com.freelancego.common.utils.SupabaseUtil;
import com.freelancego.dto.user.MilestoneDto;
import com.freelancego.dto.user.SubmissionDto;
import com.freelancego.enums.MilestoneStatus;
import com.freelancego.enums.SubmissionStatus;
import com.freelancego.enums.VerificationStatus;
import com.freelancego.exception.BadRequestException;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.exception.UnauthorizedAccessException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.MilestoneMapper;
import com.freelancego.mapper.SubmissionMapper;
import com.freelancego.model.*;
import com.freelancego.repo.*;
import com.freelancego.service.MilestoneService.MilestoneService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

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

    public MilestoneServiceImpl(UserRepository userRepository, ContractRepository contractRepository, MilestoneRepository milestoneRepository, MilestoneMapper milestoneMapper, SubmissionRepository submissionRepository, SubmissionMapper submissionMapper, ClientRepository clientRepository, FreelancerRepository freelancerRepository, SupabaseUtil supabaseUtil) {
        this.userRepository = userRepository;
        this.contractRepository = contractRepository;
        this.milestoneRepository = milestoneRepository;
        this.milestoneMapper = milestoneMapper;
        this.submissionRepository = submissionRepository;
        this.submissionMapper = submissionMapper;
        this.clientRepository = clientRepository;
        this.freelancerRepository = freelancerRepository;
        this.supabaseUtil = supabaseUtil;
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
                    "Invalid milestone number. Expected: " + nextAllowedSequence);
        }

        milestone.setContract(contract);
        milestone.setClientFeedback(null);
        milestone.setStatus(MilestoneStatus.IN_PROGRESS);

        if (milestoneDto.getSubmission() != null) {
            Submission submission = submissionMapper.toEntity(milestoneDto.getSubmission());
            milestone.setSubmission(submission);
            submissionRepository.save(submission);
        }

        Milestone saved = milestoneRepository.save(milestone);
        return milestoneMapper.toDTO(saved);
    }

    public MilestoneDto updateMilestone(MilestoneDto milestoneDto, int freelancerId, String name) {
        User user = userRepository.findByEmail(name)
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

        Milestone milestone = milestoneRepository.findById(milestoneDto.getId())
                .orElseThrow(()-> new UserNotFoundException("milestone not found"));

        if(milestone != null){
           if(milestone.getVerificationStatus() == VerificationStatus.APPROVED_BY_CLIENT && milestone.isLocked()){
               throw new BadRequestException("your are not allowed to update the approved milestone");
           }
        }
        if(milestoneDto != null && milestone != null){
            milestone.setTitle(milestoneDto.getTitle());
            milestone.setDescription(milestoneDto.getDescription());
            milestone.setAmount(milestoneDto.getAmount());
            milestone.setDueDate(milestoneDto.getDueDate());
        }
        if(milestone != null) milestoneRepository.save(milestone);
        return milestoneMapper.toDTO(milestone);
    }

    public MilestoneDto editMilestone(MilestoneDto milestoneDto, int clientId, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("client not found"));

        if(clientId != client.getId()){
            throw new UnauthorizedAccessException("your are not authorised to edit the milestone");
        }

        Milestone milestone = milestoneRepository.findById(milestoneDto.getId())
                .orElseThrow(()-> new UserNotFoundException("milestone not found"));
        if(milestone != null){
            if(milestone.isLocked() && milestone.getVerificationStatus() == VerificationStatus.APPROVED_BY_CLIENT){
                throw new BadRequestException("milestone is locked and cannot be updated");
            }
            milestone.setClientFeedback(milestoneDto.getClientFeedback());
            milestone.setVerificationStatus(VerificationStatus.CHANGES_REQUESTED);
        }
        if(milestone != null) milestoneRepository.save(milestone);
        return milestoneMapper.toDTO(milestone);
    }

    public MilestoneDto approveMilestone(int milestoneId, int clientId, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Client client = clientRepository.findByUser(user)
                .orElseThrow(()-> new UserNotFoundException("client not found"));

        if(clientId != client.getId()){
            throw new UnauthorizedAccessException("your are not authorised to edit the milestone");
        }

        Milestone milestone = milestoneRepository.findById(milestoneId)
                .orElseThrow(()-> new UserNotFoundException("milestone not found"));

        if(milestone != null){
            milestone.setVerificationStatus(VerificationStatus.APPROVED_BY_CLIENT);
            milestone.setLocked(true);
        }
        if(milestone != null) milestoneRepository.save(milestone);
        return milestoneMapper.toDTO(milestone);
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

        Milestone milestone = milestoneRepository.findById(submission.getId())
                .orElseThrow(()-> new UserNotFoundException("milestone not found"));

        if (milestone.getContract().getClient().getId() != client.getId()) {
            throw new BadRequestException("unauthorised to provide submission approval");
        }

        submission.setStatus(SubmissionStatus.APPROVED);

        if(submission != null) submissionRepository.save(submission);
        milestone.setSubmission(submission);
        milestone.setStatus(MilestoneStatus.SUBMITTED);

        if(milestone != null) milestoneRepository.save(milestone);

        return milestoneMapper.toDTO(milestone);
    }

}