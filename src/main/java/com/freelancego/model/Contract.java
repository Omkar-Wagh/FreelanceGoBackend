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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Bid getAcceptedBid() {
        return acceptedBid;
    }

    public void setAcceptedBid(Bid acceptedBid) {
        this.acceptedBid = acceptedBid;
    }

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }
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
