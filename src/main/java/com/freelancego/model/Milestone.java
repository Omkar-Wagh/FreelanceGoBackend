package com.freelancego.model;

import com.freelancego.enums.MilestoneStatus;
import com.freelancego.enums.PaymentStatus;
import com.freelancego.enums.VerificationStatus;
import jakarta.persistence.*;

import java.time.OffsetDateTime;

@Entity
public class Milestone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int milestoneNumber;
    private String title;
    private String description;
    private double amount;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false)
    private OffsetDateTime dueDate;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", nullable = false, updatable = false)
    private OffsetDateTime createdAt = OffsetDateTime.now();

    @Enumerated(EnumType.STRING)
    private MilestoneStatus status = MilestoneStatus.PENDING;

    @Enumerated(EnumType.STRING)
    private PaymentStatus paymentStatus = PaymentStatus.NOT_PAID;

    @Enumerated(EnumType.STRING)
    private VerificationStatus verificationStatus = VerificationStatus.PENDING_REVIEW;

    @ManyToOne(optional = false)
    @JoinColumn(name = "contract_id")
    private Contract contract;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "submission_id")
    private Submission submission;

    @Column(length = 1000)
    private String clientFeedback;

    private boolean locked = false;

    public void lock() { this.locked = true; }
    public boolean isLocked() { return locked; }

    public int getMilestoneProgress() {
        return switch (this.status) {
            case PENDING -> 0;
            case IN_PROGRESS -> 25;
            case SUBMITTED -> 70;
            case REVISION_REQUESTED -> 50;
            case APPROVED -> 90;
            case COMPLETED -> 100;
            case CANCELLED -> 0;
        };
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMilestoneNumber() {
        return milestoneNumber;
    }

    public void setMilestoneNumber(int milestoneNumber) {
        this.milestoneNumber = milestoneNumber;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public OffsetDateTime getDueDate() {
        return dueDate;
    }

    public void setDueDate(OffsetDateTime dueDate) {
        this.dueDate = dueDate;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public MilestoneStatus getStatus() {
        return status;
    }

    public void setStatus(MilestoneStatus status) {
        this.status = status;
    }

    public PaymentStatus getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(PaymentStatus paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public VerificationStatus getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(VerificationStatus verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }

    public Submission getSubmission() {
        return submission;
    }

    public void setSubmission(Submission submission) {
        this.submission = submission;
    }

    public String getClientFeedback() {
        return clientFeedback;
    }

    public void setClientFeedback(String clientFeedback) {
        this.clientFeedback = clientFeedback;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }
}
