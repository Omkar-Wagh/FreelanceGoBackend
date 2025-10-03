package com.freelancego.repo;

import com.freelancego.model.Client;
import com.freelancego.model.Contract;
import com.freelancego.model.Freelancer;
import com.freelancego.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract,Integer> {

    Contract findByJob(Job job);

    Contract findByJobAndClientAndFreelancer(Job job, Client client, Freelancer freelancer);

    @Query("SELECT c FROM Contract c WHERE c.job.id = :jobId AND c.client.id = :clientId AND c.freelancer.id = :freelancerId")
    Optional<Contract> findContract(@Param("jobId") int jobId, @Param("clientId") int clientId, @Param("freelancerId") int freelancerId);

    boolean existsByJobId(int jobId);
}