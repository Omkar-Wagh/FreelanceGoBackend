package com.freelancego.service.FreelancerService.impl;

import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.enums.BidStatus;
import com.freelancego.exception.InternalServerErrorException;
import com.freelancego.exception.InvalidIdException;
import com.freelancego.exception.UserNotFoundException;
import com.freelancego.mapper.BidMapper;
import com.freelancego.model.Bid;
import com.freelancego.model.Freelancer;
import com.freelancego.model.Job;
import com.freelancego.model.User;
import com.freelancego.repo.BidRepository;
import com.freelancego.repo.FreelancerRepository;
import com.freelancego.repo.JobRepository;
import com.freelancego.repo.UserRepository;
import com.freelancego.service.FreelancerService.BidService;
import org.springframework.stereotype.Service;

@Service
public class BidServiceImpl implements BidService {

    final private BidMapper bidMapper;
    final private JobRepository jobRepository;
    final private FreelancerRepository freelancerRepository;
    final private BidRepository bidRepository;
    final private UserRepository userRepository;

    public BidServiceImpl(BidMapper bidMapper, JobRepository jobRepository, FreelancerRepository freelancerRepository, BidRepository bidRepository, UserRepository userRepository) {
        this.bidMapper = bidMapper;
        this.jobRepository = jobRepository;
        this.freelancerRepository = freelancerRepository;
        this.bidRepository = bidRepository;
        this.userRepository = userRepository;
    }

    public BidDto createBid(BidDto bidDto, String name) {
        Bid bid = bidMapper.toEntity(bidDto);

        User user = userRepository.findByEmail(name).orElseThrow(
                ()-> new UserNotFoundException("User not found"));

        if (bid.getFreelancer() == null || bid.getFreelancer().getId() <= 0) {
            throw new InvalidIdException("Freelancer information is missing in bid request");
        }

        if (bid.getJob() == null || bid.getJob().getId() <= 0) {
            throw new InvalidIdException("Job information is missing in bid request");
        }

        Job job = jobRepository.findById(bid.getJob().getId()).orElseThrow(
                () -> new UserNotFoundException("Job Not Found For Id" + bid.getJob().getId()));

        Freelancer freelancer = freelancerRepository.findByUser(user)
                .orElseThrow(() -> new UserNotFoundException("Freelancer not found for user " + user.getId()));

        if (bid.getFreelancer().getUser().getId() != user.getId()) {
            throw new InvalidIdException("You are not authorized to place a bid as this freelancer");
        }

        if (bidRepository.existsByJobIdAndFreelancerId(job.getId(), freelancer.getId())) {
            throw new InvalidIdException("You already placed a bid on this job.");
        }
        try{
            bid.setJob(job);
            bid.setFreelancer(freelancer);
            bid.setStatus(BidStatus.PENDING);
            bidRepository.save(bid);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new InternalServerErrorException("Something went wrong while creating the bid");
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
