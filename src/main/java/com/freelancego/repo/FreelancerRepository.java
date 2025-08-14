package com.freelancego.repo;

import com.freelancego.model.Freelancer;
import com.freelancego.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FreelancerRepository extends JpaRepository<Freelancer, Integer> {
    Freelancer findByUser(User user);
}
