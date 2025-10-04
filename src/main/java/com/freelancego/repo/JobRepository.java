package com.freelancego.repo;

import com.freelancego.enums.JobPhase;
import com.freelancego.enums.JobStatus;
import com.freelancego.model.Client;
import com.freelancego.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {
    Page<Job> findJobByClient(Client client, Pageable pageable);
    List<Job> findByClientIdAndStatusAndPhaseIn(int id, JobStatus status, List<JobPhase> phases);
    List<Job> findByClientIdAndStatusAndPhase(int id, JobStatus status, JobPhase phases);
    List<Job> findByClientIdAndStatus(int clientId, JobStatus status);
    List<Job> findByClientIdAndPhase(int id, JobPhase jobPhase);
    Page<Job> findByStatus(Pageable pageable, JobStatus jobStatus);
    Page<Job> findJobByClientAndStatus(Client client, JobStatus jobStatus, Pageable pageable);
}
