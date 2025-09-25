package com.freelancego.model;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "chat_history")
public class ChatHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // saved the user twice for both owner and another history
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_id", nullable = false, unique = true)
    private User owner;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "another_id")
    private User opponent;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "chat_history_jobs",
            joinColumns = @JoinColumn(name = "chat_history_id"),
            inverseJoinColumns = @JoinColumn(name = "job_id")
    )
    private Set<Job> jobs;

    @OneToMany(mappedBy = "history", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<ChatMessage> chats;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "last_chat_id")
    private ChatMessage lastChat;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    public ChatHistory(User owner) {
        this.owner = owner;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getOwner() { return owner; }
    public void setOwner(User owner) { this.owner = owner; }

    public User getAnother() { return opponent; }
    public void setAnother(User opponent) { this.opponent = opponent; }

    public Set<Job> getJobs() { return jobs; }
    public void setJobs(Set<Job> jobs) { this.jobs = jobs; }

    public List<ChatMessage> getChats() { return chats; }
    public void setChats(List<ChatMessage> chats) { this.chats = chats; }

    public ChatMessage getLastChat() {
        return lastChat;
    }

    public void setLastChat(ChatMessage lastChat) {
        this.lastChat = lastChat;
    }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
