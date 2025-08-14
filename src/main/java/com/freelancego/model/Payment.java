package com.freelancego.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Payment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Double amount;
    private LocalDateTime paidAt;
    private String status; // PENDING, COMPLETED, FAILED, REFUNDED
    private String method; // CARD, UPI, BANK_TRANSFER

    @ManyToOne
    private Contract contract;

    @ManyToOne
    private User payer;

    @ManyToOne
    private User payee;
}
