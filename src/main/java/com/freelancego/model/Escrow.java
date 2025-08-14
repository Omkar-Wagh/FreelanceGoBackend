package com.freelancego.model;

import com.freelancego.enums.EscrowStatus;
import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Escrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Double heldAmount;

    @Enumerated(EnumType.STRING)
    private EscrowStatus status; // HELD, RELEASED, REFUNDED

    private LocalDateTime createdAt;
    private LocalDateTime releasedAt;
    private LocalDateTime refundedAt;

    @OneToOne(optional = false)
    private Contract contract;
    // Getters and setters
}
