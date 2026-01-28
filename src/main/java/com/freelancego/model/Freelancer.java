package com.freelancego.model;

import com.freelancego.enums.ExperienceLevel;
import com.freelancego.enums.PayoutAccountStatus;
import jakarta.persistence.*;

@Entity
public class Freelancer {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @MapsId
    @JoinColumn(name = "id")
    private User user;
    private String designation;
    private String bio;
    private String portfolioUrl;
    private String skills;
    private ExperienceLevel experienceLevel;
    private String phone;

    @Column(unique = true)
    private String razorpayContactId;

    @Column(unique = true)
    private String razorpayFundAccountId;

    @Enumerated(EnumType.STRING)
    private PayoutAccountStatus payoutAccountStatus;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getSkills() {
        return skills;
    }

    public void setSkills(String skills) {
        this.skills = skills;
    }


    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getRazorpayContactId() {
        return razorpayContactId;
    }

    public void setRazorpayContactId(String razorpayContactId) {
        this.razorpayContactId = razorpayContactId;
    }

    public String getRazorpayFundAccountId() {
        return razorpayFundAccountId;
    }

    public void setRazorpayFundAccountId(String razorpayFundAccountId) {
        this.razorpayFundAccountId = razorpayFundAccountId;
    }

    public PayoutAccountStatus getPayoutAccountStatus() {
        return payoutAccountStatus;
    }

    public void setPayoutAccountStatus(PayoutAccountStatus payoutAccountStatus) {
        this.payoutAccountStatus = payoutAccountStatus;
    }
}
