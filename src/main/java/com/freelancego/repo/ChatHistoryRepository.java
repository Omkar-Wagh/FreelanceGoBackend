package com.freelancego.repo;

import com.freelancego.model.ChatHistory;
import com.freelancego.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ChatHistoryRepository extends JpaRepository<ChatHistory, Integer> {

    boolean existsByOwnerAndOpponent(User owner, User opponent);
    Page<ChatHistory> findByOwner(User owner, Pageable pageable);
}
