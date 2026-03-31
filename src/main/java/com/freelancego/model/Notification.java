package com.freelancego.model;

import com.freelancego.enums.NotificationType;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    // The user who receives the notification
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    // The user who triggered the notification (e.g., new job, new bid, etc.)
    @ManyToOne
    @JoinColumn(name = "trigger_user_id")
    private User triggerUser;

    /**
    * Set to type 'String' for avoiding any issues with enum persistence
    * Will not create problem even after changing the order of enum constants
    */
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private OffsetDateTime createdAt;
    private boolean isSeen;

    private boolean isGeneral;

    public Notification() {
    }

    public Notification(User user, User triggerUser, NotificationType type) {
        this.user = user;
        this.triggerUser = triggerUser;
        this.type = type;
    }

    public Notification(User triggerUser, NotificationType type, boolean isGeneral) {
        this.triggerUser = triggerUser;
        this.type = type;
        this.isGeneral = isGeneral;
    }

    @PrePersist
    public void onCreate() {
        this.createdAt = OffsetDateTime.now();
        this.isSeen = false; // default to unseen when created
    }

    public boolean isGeneral() {
        return isGeneral;
    }

    public void setGeneral(boolean general) {
        isGeneral = general;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getTriggerUser() {
        return triggerUser;
    }

    public void setTriggerUser(User triggerUser) {
        this.triggerUser = triggerUser;
    }

    public NotificationType getType() {
        return type;
    }

    public void setType(NotificationType type) {
        this.type = type;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isSeen() {
        return isSeen;
    }

    public void setSeen(boolean seen) {
        isSeen = seen;
    }
}
