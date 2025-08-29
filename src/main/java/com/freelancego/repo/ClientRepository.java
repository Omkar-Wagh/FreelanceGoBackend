package com.freelancego.repo;

import com.freelancego.model.Client;
import com.freelancego.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Optional<Client> findByUser(User user);
}
