package com.freelancego.repo;

import com.freelancego.model.Client;
import com.freelancego.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClientRepository extends JpaRepository<Client, Integer> {
    Client findByUser(User user);
}
