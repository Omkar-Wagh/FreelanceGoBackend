package com.freelancego.repo;

import com.freelancego.enums.ContractStatus;
import com.freelancego.model.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContractRepository extends JpaRepository<Contract,Integer> {

    Contract findByJob(Job job);

    Contract findByJobAndClientAndFreelancer(Job job, Client client, Freelancer freelancer);

    @Query("SELECT c FROM Contract c WHERE c.job.id = :jobId AND c.client.id = :clientId AND c.freelancer.id = :freelancerId")
    Optional<Contract> findContract(@Param("jobId") int jobId, @Param("clientId") int clientId, @Param("freelancerId") int freelancerId);

    boolean existsByJobId(int jobId);

    Page<Contract> findByClientAndStatus(Client client, ContractStatus status,Pageable pageable);

    List<Contract> findByClientAndStatus(Client client, ContractStatus status);

    List<Contract> findByClient(Client client);

    List<Contract> findByFreelancer(Freelancer freelancer);

    Page<Contract> findByFreelancer(Freelancer freelancer, Pageable pageable);

    List<Contract> findByFreelancerAndStatus(Freelancer freelancer, ContractStatus contractStatus);

    Contract findByAcceptedBid(Bid bid);

}
