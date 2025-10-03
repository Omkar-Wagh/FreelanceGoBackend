package com.freelancego.dto.user;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.dto.freelancer.FreelancerDto;

import java.time.OffsetDateTime;

public record ContractDto(int id, String status, OffsetDateTime createdAt, BidDto bid, JobDto job, ClientDto client, FreelancerDto freelancer) {
}
