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
import com.freelancego.repo.BidRepository;
import com.freelancego.repo.FreelancerRepository;
import com.freelancego.repo.JobRepository;
import com.freelancego.service.FreelancerService.BidService;
import org.springframework.stereotype.Service;

@Service
public class BidServiceImpl implements BidService {

    final private BidMapper bidMapper;
    final private JobRepository jobRepository;
    final private FreelancerRepository freelancerRepository;
    final private BidRepository bidRepository;

    public BidServiceImpl(BidMapper bidMapper, JobRepository jobRepository, FreelancerRepository freelancerRepository, BidRepository bidRepository) {
        this.bidMapper = bidMapper;
        this.jobRepository = jobRepository;
        this.freelancerRepository = freelancerRepository;
        this.bidRepository = bidRepository;
    }

    public BidDto createBid(BidDto bidDto, String name) {
        Bid bid = bidMapper.toEntity(bidDto);

        if(bid.getFreelancer().getId() <= 0){
            throw new UserNotFoundException("Freelancer for Bid with Id " + bid.getFreelancer().getId() + "Not Found");
        }

        if(bid.getJob().getId() <= 0){
            throw new UserNotFoundException("Job not found for job Id " + bid.getJob().getId());
        }

        Job job = jobRepository.findById(bid.getJob().getId()).orElseThrow(
                () -> new UserNotFoundException("Job Not Found For Id" + bid.getJob().getId())
        );

        Freelancer freelancer = freelancerRepository.findById(bid.getFreelancer().getId()).orElseThrow(
                () -> new UserNotFoundException("Freelancer not found for Id " + bid.getFreelancer().getId())
        );

        bid.setJob(job);
        bid.setFreelancer(freelancer);
        bid.setStatus(BidStatus.PENDING);

        if (bidRepository.existsByJobIdAndFreelancerId(job.getId(), freelancer.getId())) {
            throw new InvalidIdException("You already placed a bid on this job.");
        }
        try{
            bidRepository.save(bid);
        }
        catch (Exception e){
            e.printStackTrace();
            throw new InternalServerErrorException("Something went wrong while creating the bid");
        }
        return bidMapper.toDto(bid);
    }

    public BidDto updateBid(BidDto bidDto, String name) {
        if(bidDto.id() <= 0){
            throw  new InvalidIdException("Bid Id is required");
        }
        Bid bid = bidRepository.findById(bidDto.id()).orElseThrow(
                () -> new UserNotFoundException("Bid not found for Id " + bidDto.id())
        );
        if(bid.isEditable()){
            bid.setCoverLetter(bidDto.coverLetter());
            bid.setTimeRequired(bidDto.timeRequired());
            bid.setAmount(bidDto.amount());
            bidRepository.save(bid);
        }
        return bidMapper.toDto(bid);
    }

}
