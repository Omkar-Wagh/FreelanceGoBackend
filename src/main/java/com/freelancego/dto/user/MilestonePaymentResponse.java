package com.freelancego.dto.user;

import com.freelancego.model.Milestone;
import com.freelancego.model.Payment;

import java.time.OffsetDateTime;

public class MilestonePaymentResponse {
    private int milestoneId;
    private double amount;
    private String currency;
    private String orderId;
    private String razorpayKey;
    private OffsetDateTime expiresAt;

    public static MilestonePaymentResponse from(Payment attempt, Milestone milestone, String key) {
        MilestonePaymentResponse r = new MilestonePaymentResponse();
        r.milestoneId = milestone.getId();
        r.amount = attempt.getAmount();
        r.currency = attempt.getCurrency();
        r.orderId = attempt.getRazorpayOrderId();
        r.expiresAt = attempt.getExpiresAt();
        r.razorpayKey = key;
        return r;
    }

    public int getMilestoneId() {
        return milestoneId;
    }

    public void setMilestoneId(int milestoneId) {
        this.milestoneId = milestoneId;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getRazorpayKey() {
        return razorpayKey;
    }

    public void setRazorpayKey(String razorpayKey) {
        this.razorpayKey = razorpayKey;
    }

    public OffsetDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(OffsetDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
}
