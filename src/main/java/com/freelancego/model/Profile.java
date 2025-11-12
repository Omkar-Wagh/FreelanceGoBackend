package com.freelancego.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Profile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @OneToOne
    @JoinColumn(name = "freelancer_id")
    private Freelancer freelancer;

    @Embedded
    private ProfileDetails clientProfile;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "bannerUrl", column = @Column(name = "freelancer_banner_url")),
            @AttributeOverride(name = "linkedinUrl", column = @Column(name = "freelancer_linkedin_url")),
            @AttributeOverride(name = "githubUrl", column = @Column(name = "freelancer_github_url")),
            @AttributeOverride(name = "location", column = @Column(name = "freelancer_location")),
            @AttributeOverride(name = "rating", column = @Column(name = "freelancer_rating"))
    })
    private ProfileDetails freelancerProfile;

    @ElementCollection
    @CollectionTable(
            name = "freelancer_portfolio_details",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    private List<PortfolioDetails> freelancerPortfolioDetails = new ArrayList<>();

    @ElementCollection
    @CollectionTable(
            name = "freelancer_certifications",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    private List<CertificationsDetails> freelancerCertificationDetails = new ArrayList<>();

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public Client getClient() { return client; }
    public void setClient(Client client) { this.client = client; }

    public Freelancer getFreelancer() { return freelancer; }
    public void setFreelancer(Freelancer freelancer) { this.freelancer = freelancer; }

    public ProfileDetails getClientProfile() { return clientProfile; }
    public void setClientProfile(ProfileDetails clientProfile) { this.clientProfile = clientProfile; }

    public ProfileDetails getFreelancerProfile() { return freelancerProfile; }
    public void setFreelancerProfile(ProfileDetails freelancerProfile) { this.freelancerProfile = freelancerProfile; }

    public List<PortfolioDetails> getFreelancerPortfolioDetails() { return freelancerPortfolioDetails; }
    public void setFreelancerPortfolioDetails(List<PortfolioDetails> freelancerPortfolioDetails) { this.freelancerPortfolioDetails = freelancerPortfolioDetails; }

    public List<CertificationsDetails> getFreelancerCertificationDetails() { return freelancerCertificationDetails; }
    public void setFreelancerCertificationDetails(List<CertificationsDetails> freelancerCertificationDetails) { this.freelancerCertificationDetails = freelancerCertificationDetails; }
}
