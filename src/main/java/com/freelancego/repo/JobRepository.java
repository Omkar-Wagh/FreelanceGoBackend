package com.freelancego.repo;

import com.freelancego.enums.JobPhase;
import com.freelancego.enums.JobStatus;
import com.freelancego.model.Client;
import com.freelancego.model.Freelancer;
import com.freelancego.model.Job;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.OffsetDateTime;
import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {
    Page<Job> findJobByClient(Client client, Pageable pageable);
    List<Job> findByClientIdAndStatusAndPhaseIn(int id, JobStatus status, List<JobPhase> phases);
    List<Job> findByClientIdAndStatusAndPhase(int id, JobStatus status, JobPhase phases);
    List<Job> findByClientIdAndStatus(int clientId, JobStatus status);
    List<Job> findByClientIdAndPhase(int id, JobPhase jobPhase);
    Page<Job> findByStatus(Pageable pageable, JobStatus jobStatus);
    Page<Job> findJobByClientAndStatus(Client client, JobStatus jobStatus, Pageable pageable);
    Page<Job> findByClient(Client client, Pageable pageable);
    List<Job> findByClient(Client client);
    Page<Job> findJobByStatus(JobStatus jobStatus, Pageable pageable);

    @Query("""
       SELECT DISTINCT j 
       FROM Job j 
       JOIN j.bids b 
       WHERE b.freelancer.id = :freelancerId
       ORDER BY j.createdAt DESC
       """)
    Page<Job> findJobsBidByFreelancer(@Param("freelancerId") int freelancerId, Pageable pageable);

    @Query("SELECT COUNT(j) FROM Job j JOIN j.bids b WHERE j.status = :status AND b.freelancer.id = :freelancerId")
    long countByActiveBids(@Param("status") JobStatus status, @Param("freelancerId") int freelancerId);

    @Modifying
    @Transactional
    @Query("UPDATE Job j SET j.status = :inactiveStatus WHERE j.createdAt <= :thresholdDate")
    void setJobStatusToInactive(@Param("thresholdDate") OffsetDateTime thresholdDate,
                                @Param("inactiveStatus") JobStatus inactiveStatus);

}
