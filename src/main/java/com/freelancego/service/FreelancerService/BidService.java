package com.freelancego.service.FreelancerService;

import com.freelancego.dto.freelancer.BidDto;

public interface BidService {
    BidDto createBid(BidDto bidDto, String name);

    BidDto updateBid(BidDto bidDto, String name);
}
