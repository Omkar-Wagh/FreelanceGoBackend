package com.freelancego.repo;

import com.freelancego.model.Freelancer;
import com.freelancego.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Integer> {
    Optional<Freelancer> findByUser(User user);
}
