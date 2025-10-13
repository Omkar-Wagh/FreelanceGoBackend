package com.freelancego.service.FreelancerService.impl;

import com.freelancego.common.utils.SupabaseUtil;
import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.enums.BidStatus;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.exception.InvalidIdException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.BidMapper;
import com.freelancego.model.*;
import com.freelancego.repo.BidRepository;
import com.freelancego.repo.FreelancerRepository;
import com.freelancego.repo.JobRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.FreelancerService.BidService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;


@Service
public class BidServiceImpl implements BidService {

    final private BidMapper bidMapper;
    final private JobRepository jobRepository;
    final private FreelancerRepository freelancerRepository;
    final private BidRepository bidRepository;
    final private UserRepository userRepository;
    final private SupabaseUtil supabaseUtil;

    public BidServiceImpl(BidMapper bidMapper, JobRepository jobRepository, FreelancerRepository freelancerRepository, BidRepository bidRepository, UserRepository userRepository, SupabaseUtil supabaseUtil) {
        this.bidMapper = bidMapper;
        this.jobRepository = jobRepository;
        this.freelancerRepository = freelancerRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
        this.supabaseUtil = supabaseUtil;
    }

    public BidDto createBid(BidDto bidDto, MultipartFile file, String name) {
        Bid bid = bidMapper.toMapEntity(bidDto);
        int freelancerId = bidDto.freelancerDto().id();
        int jobId = bidDto.jobDto().id();

        User user = userRepository.findByEmail(name).orElseThrow(
                ()-> new UserNotFoundException("User not found"));

        Job job = jobRepository.findById(jobId).orElseThrow(
                () -> new UserNotFoundException("Job Not Found For Id" + jobId));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found for user " + user.getId()));


        if (freelancer.getUser().getId() != user.getId()) {
            throw new InvalidIdException("You are not authorized to place a bid as this freelancer");
        }

        if (job.getClient().getUser().getId() == user.getId()) {
            throw new InvalidIdException("You cannot place a bid on your own job.");
        }

        if (bidRepository.existsByJobIdAndFreelancerId(job.getId(), freelancer.getId())) {
            throw new InvalidIdException("You already placed a bid on this job.");
        }

        try{
            bid.setJob(job);
            bid.setFreelancer(freelancer);
            bid.setStatus(BidStatus.PENDING);

            String regex = ".*\\.(pdf|png|jpg|jpeg|pptx|docx)$";

            if (file != null && file.getOriginalFilename().matches(regex)) {
                String attachmentPublicUrl = supabaseUtil.uploadFile(file);
                bid.setAttachmentPublicUrl(attachmentPublicUrl);
            }

            bidRepository.save(bid);
        }
        catch (Exception e){
            throw new InternalServerErrorException("Something went wrong while creating the bid " + e.getMessage());
        }

        return bidMapper.toDto(bid);
    }

    public BidDto updateBid(BidDto bidDto, String name) {
        User user = userRepository.findByEmail(name)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found for user " + user.getEmail()));

        Bid bid = bidRepository.findById(bidDto.id())
                .orElseThrow(() -> new UserNotFoundException("Bid not found for Id " + bidDto.id()));

        if (bid.getFreelancer().getId() != freelancer.getId()) {
            throw new InvalidIdException("You cannot edit someone else's bid");
        }

        if (!bid.isEditable()) {
            throw new InvalidIdException("This bid is locked and cannot be edited");
        }

        bid.setCoverLetter(bidDto.coverLetter());
        bid.setTimeRequired(bidDto.timeRequired());
        bid.setAmount(bidDto.amount());
        bidRepository.save(bid);
        return bidMapper.toDto(bid);
    }

}
