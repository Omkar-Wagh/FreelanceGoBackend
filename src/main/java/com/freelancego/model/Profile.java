package com.freelancego.model;

import jakarta.persistence.*;

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

    @Embedded
    private PortfolioDetails clientPortfolioDetails;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "portfolioUrl", column = @Column(name = "freelancer_portfolio_url")),
            @AttributeOverride(name = "imageUrl", column = @Column(name = "freelancer_image_url")),
            @AttributeOverride(name = "title", column = @Column(name = "freelancer_title")),
            @AttributeOverride(name = "description", column = @Column(name = "freelancer_description")),
    })
    private PortfolioDetails freelancerPortfolioDetails;

    @Embedded
    private CertificationsDetails clientCertificationDetails;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "certificateName", column = @Column(name = "freelancer_certificateName")),
            @AttributeOverride(name = "provider", column = @Column(name = "freelancer_provider")),
            @AttributeOverride(name = "certificateUrl", column = @Column(name = "freelancer_certificateUrl")),
    })
    private CertificationsDetails freelancerCertificationDetails;

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

    public ProfileDetails getClientProfile() {
        return clientProfile;
    }

    public void setClientProfile(ProfileDetails clientProfile) {
        this.clientProfile = clientProfile;
    }

    public ProfileDetails getFreelancerProfile() {
        return freelancerProfile;
    }

    public void setFreelancerProfile(ProfileDetails freelancerProfile) {
        this.freelancerProfile = freelancerProfile;
    }

    public PortfolioDetails getClientPortfolioDetails() {
        return clientPortfolioDetails;
    }

    public void setClientPortfolioDetails(PortfolioDetails clientPortfolioDetails) {
        this.clientPortfolioDetails = clientPortfolioDetails;
    }

    public PortfolioDetails getFreelancerPortfolioDetails() {
        return freelancerPortfolioDetails;
    }

    public void setFreelancerPortfolioDetails(PortfolioDetails freelancerPortfolioDetails) {
        this.freelancerPortfolioDetails = freelancerPortfolioDetails;
    }

    public CertificationsDetails getClientCertificationDetails() {
        return clientCertificationDetails;
    }

    public void setClientCertificationDetails(CertificationsDetails clientCertificationDetails) {
        this.clientCertificationDetails = clientCertificationDetails;
    }

    public CertificationsDetails getFreelancerCertificationDetails() {
        return freelancerCertificationDetails;
    }

    public void setFreelancerCertificationDetails(CertificationsDetails freelancerCertificationDetails) {
        this.freelancerCertificationDetails = freelancerCertificationDetails;
    }
}
