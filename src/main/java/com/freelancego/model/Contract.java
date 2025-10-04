package com.freelancego.model;

import com.freelancego.enums.ContractStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
public class Contract {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Enumerated(EnumType.STRING)
    private ContractStatus status; // ACTIVE, COMPLETED, CANCELLED

    private OffsetDateTime createdAt;

    @OneToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "bid_id")
    private Bid acceptedBid;

    @OneToOne(optional = false, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    private Client client;

    @ManyToOne
    private Freelancer freelancer;

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public OffsetDateTime getCreateAt() {
        return createdAt;
    }

    public void setCreateAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
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

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Freelancer getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(Freelancer freelancer) {
        this.freelancer = freelancer;
    }
}
