package com.freelancego.service.Freelancer;

import com.freelancego.dto.freelancer.BidDto;
import org.springframework.web.multipart.MultipartFile;

public interface BidService {
    BidDto createBid(BidDto bidDto, MultipartFile file, String name);

    BidDto updateBid(BidDto bidDto, String name);
}
