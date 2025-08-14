package com.freelancego.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private String status; // ACTIVE, COMPLETED, CANCELLED

    @OneToOne(optional = false)
    @JoinColumn(name = "bid_id", unique = true)
    private Bid acceptedBid;

    @OneToOne(optional = false)
    @JoinColumn(name = "job_id", unique = true)
    private Job job;

    // Getters and Setters
}

//@Query("SELECT c FROM Contract c " +
//        "WHERE c.acceptedBid.freelancer.id = :freelancerId " +
//        "AND c.job.client.id = :clientId")
// or else
//
//@Entity
//public class Contract {
//    @Id
//    @GeneratedValue
//    private Long id;
//
//    private LocalDate startDate;
//    private LocalDate endDate;
//
//    private String status; // ACTIVE, COMPLETED, CANCELLED
//
//    @OneToOne
//    private Bid acceptedBid;
//
//    @OneToOne
//    private Job job;
//
//    @ManyToOne
//    private Client client; // <-- Added directly
//
//    @ManyToOne
//    private Freelancer freelancer; // <-- Added directly
//}
