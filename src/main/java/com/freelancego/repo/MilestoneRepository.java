package com.freelancego.repo;

import com.freelancego.model.Contract;
import com.freelancego.model.Milestone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone,Integer> {
    List<Milestone> findByContract(Contract contract);
}

