package com.freelancego.service.ClientService.impl;

import com.freelancego.enums.JobStatus;
import com.freelancego.repo.JobRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class CleanupService {

    private final JobRepository jobRepository;

    public CleanupService(JobRepository jobRepository) {
        this.jobRepository = jobRepository;
    }

    @Scheduled(fixedRate = 300000)
    public void cleanExpiredJobs() {
        OffsetDateTime thresholdDate = OffsetDateTime.now().minusDays(2);
        jobRepository.setJobStatusToInactive(thresholdDate, JobStatus.INACTIVE);
    }
}