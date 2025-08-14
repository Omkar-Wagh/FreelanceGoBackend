package com.freelancego.model;

import com.freelancego.enums.MilestoneStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String description;

    private Double amount;

    @Enumerated(EnumType.STRING)
    private MilestoneStatus status;

    private LocalDateTime createdAt;
    private LocalDateTime releasedAt;
    private LocalDateTime completedAt;

    @ManyToOne(optional = false)
    private Contract contract;

    @OneToOne(optional = true)
    @JoinColumn(name = "payment_id")
    private Payment payment;
}
