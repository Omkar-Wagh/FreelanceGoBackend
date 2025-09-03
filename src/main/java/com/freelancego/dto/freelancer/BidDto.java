package com.freelancego.dto.freelancer;

import java.time.OffsetDateTime;

public record BidDto(
        int id,
        Double amount,
        String coverLetter,
        OffsetDateTime submittedAt,
        FreelancerDto freelancerDto
) {
}
