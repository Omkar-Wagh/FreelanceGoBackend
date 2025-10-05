package com.freelancego.repo;

import com.freelancego.model.Bid;
import com.freelancego.model.Freelancer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BidRepository extends JpaRepository<Bid, Integer> {
    boolean existsByJobIdAndFreelancerId(int jobId, int freelancerId);

    List<Bid> findByFreelancer(Freelancer freelancer);
}
