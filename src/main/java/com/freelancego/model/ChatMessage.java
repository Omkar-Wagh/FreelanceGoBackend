package com.freelancego.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
public class ChatMessage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int senderId;
    private int receiverId;
    private String content;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false, nullable = false)
    private OffsetDateTime timestamp;

    @ManyToOne(fetch = FetchType.LAZY)
    private ChatHistory history;

    @PrePersist
    protected void onCreate() {
        this.timestamp = OffsetDateTime.now();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSenderId() {
        return senderId;
    }

    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }

    public int getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(OffsetDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public ChatHistory getHistory() {
        return history;
    }

    public void setHistory(ChatHistory history) {
        this.history = history;
    }
}