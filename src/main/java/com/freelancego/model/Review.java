package com.freelancego.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private Integer rating; // e.g. 1–5
    private String feedback;

    @ManyToOne(optional = false)
    private Contract contract;

    @ManyToOne(optional = false)
    private User reviewer;  // The user who gave the review

    @ManyToOne(optional = false)
    private User reviewee;  // The user receiving the review

    private LocalDateTime createdAt;
}
