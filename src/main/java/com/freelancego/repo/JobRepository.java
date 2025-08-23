package com.freelancego.repo;

import com.freelancego.model.Client;
import com.freelancego.model.Job;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JobRepository extends JpaRepository<Job, Integer> {
    List<Job> getPostByClient(Client client);
}
