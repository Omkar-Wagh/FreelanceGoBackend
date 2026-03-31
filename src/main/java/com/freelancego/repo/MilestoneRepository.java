package com.freelancego.repo;

import com.freelancego.model.Contract;
import com.freelancego.model.Milestone;
import com.freelancego.model.Submission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.List;

@Repository
public interface MilestoneRepository extends JpaRepository<Milestone,Integer> {
    List<Milestone> findByContract(Contract contract);

    Milestone findBySubmission(Submission submission);
}

