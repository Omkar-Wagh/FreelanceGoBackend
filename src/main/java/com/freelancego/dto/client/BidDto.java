package com.freelancego.dto.client;

import com.freelancego.dto.freelancer.FreelancerDto;
import java.time.LocalDateTime;

public record BidDto(
        int id,
        Double amount,
        String coverLetter,
        LocalDateTime submittedAt,
        JobDto jobDto,
        FreelancerDto freelancerDto
) {
}
