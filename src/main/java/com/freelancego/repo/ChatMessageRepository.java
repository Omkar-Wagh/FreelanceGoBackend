package com.freelancego.repo;

import com.freelancego.model.ChatHistory;
import com.freelancego.model.ChatMessage;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChatMessageRepository extends JpaRepository<ChatMessage, Integer> {
    @Query("SELECT m FROM ChatMessage m WHERE " +
            "(m.senderId = :senderId AND m.receiverId = :receiverId) OR " +
            "(m.senderId = :receiverId AND m.receiverId = :senderId) " +
            "ORDER BY m.timestamp ASC")
    List<ChatMessage> findConversation(@Param("senderId") int senderId, @Param("receiverId") int receiverId);

    List<ChatMessage> findBySenderIdAndReceiverIdOrderByTimestampDesc(int senderId, int receiverId, Pageable pageable);
}

