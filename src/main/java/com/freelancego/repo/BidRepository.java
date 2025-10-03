package com.freelancego.repo;

import com.freelancego.model.Bid;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {
    boolean existsByJobIdAndFreelancerId(int jobId, int freelancerId);

}
