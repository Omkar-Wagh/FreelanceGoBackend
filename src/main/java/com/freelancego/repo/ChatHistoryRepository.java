package com.freelancego.repo;

import com.freelancego.model.ChatHistory;
import com.freelancego.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Integer> {

    boolean existsByOwnerAndOpponent(User owner, User opponent);
    List<ChatHistory> findByOwner(User owner);
}
