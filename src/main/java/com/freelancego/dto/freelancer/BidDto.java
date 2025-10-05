package com.freelancego.dto.freelancer;

import com.freelancego.dto.client.JobDto;
import java.time.OffsetDateTime;

public record BidDto(
        int id,
        long amount,
        String coverLetter,
        OffsetDateTime submittedAt,
        String timeRequired,
        String status,
        JobDto jobDto,
        FreelancerDto freelancerDto
) {
}
