package com.freelancego.dto.user;

import java.time.OffsetDateTime;

public class MilestoneDto {

    private int id;
    private int milestoneNumber;
    private String title;
    private String description;
    private double amount;
    private OffsetDateTime dueDate;
    private OffsetDateTime createdAt;

    private String status;
    private String paymentStatus;
    private String verificationStatus;

    private ContractDto contract;

    private SubmissionDto submission;

    private String clientFeedback;
    private boolean locked;

    private int milestoneProgress;

    // Getters and Setters

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public ContractDto getContract() {
        return contract;
    }

    public void setContract(ContractDto contract) {
        this.contract = contract;
    }

    public SubmissionDto getSubmission() {
        return submission;
    }

    public void setSubmission(SubmissionDto submission) {
        this.submission = submission;
    }

    public String getClientFeedback() {
        return clientFeedback;
    }

    public void setClientFeedback(String clientFeedback) {
        this.clientFeedback = clientFeedback;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int getMilestoneProgress() {
        return milestoneProgress;
    }

    public void setMilestoneProgress(int milestoneProgress) {
        this.milestoneProgress = milestoneProgress;
    }
}
