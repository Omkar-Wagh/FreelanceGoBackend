package com.freelancego.repo;

import com.freelancego.model.Bid;
import com.freelancego.model.Freelancer;
import com.freelancego.model.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {
    boolean existsByJobIdAndFreelancerId(int jobId, int freelancerId);

    List<Bid> findByFreelancer(Freelancer freelancer);

    Page<Bid> findByJob(Job job, Pageable pageable);

    @Query("SELECT COUNT(b) FROM Bid b WHERE b.job.id = :jobId")
    int countBidsByJobId(@Param("jobId") int jobId);
}
