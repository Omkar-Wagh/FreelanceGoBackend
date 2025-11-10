package com.freelancego.dto.user;

public class ProfileDto {
    private int id;
    private int userId;
    private int clientId;
    private int freelancerId;

    private ProfileDetailsDto clientProfile;
    private ProfileDetailsDto freelancerProfile;

    private PortfolioDetailsDto clientPortfolioDetails;
    private PortfolioDetailsDto freelancerPortfolioDetails;

    private CertificationDetailsDto clientCertificationDetails;
    private CertificationDetailsDto freelancerCertificationDetails;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getFreelancerId() {
        return freelancerId;
    }

    public void setFreelancerId(int freelancerId) {
        this.freelancerId = freelancerId;
    }

    public ProfileDetailsDto getClientProfile() {
        return clientProfile;
    }

    public void setClientProfile(ProfileDetailsDto clientProfile) {
        this.clientProfile = clientProfile;
    }

    public ProfileDetailsDto getFreelancerProfile() {
        return freelancerProfile;
    }

    public void setFreelancerProfile(ProfileDetailsDto freelancerProfile) {
        this.freelancerProfile = freelancerProfile;
    }

    public PortfolioDetailsDto getClientPortfolioDetails() {
        return clientPortfolioDetails;
    }

    public void setClientPortfolioDetails(PortfolioDetailsDto clientPortfolioDetails) {
        this.clientPortfolioDetails = clientPortfolioDetails;
    }

    public PortfolioDetailsDto getFreelancerPortfolioDetails() {
        return freelancerPortfolioDetails;
    }

    public void setFreelancerPortfolioDetails(PortfolioDetailsDto freelancerPortfolioDetails) {
        this.freelancerPortfolioDetails = freelancerPortfolioDetails;
    }

    public CertificationDetailsDto getClientCertificationDetails() {
        return clientCertificationDetails;
    }

    public void setClientCertificationDetails(CertificationDetailsDto clientCertificationDetails) {
        this.clientCertificationDetails = clientCertificationDetails;
    }

    public CertificationDetailsDto getFreelancerCertificationDetails() {
        return freelancerCertificationDetails;
    }

    public void setFreelancerCertificationDetails(CertificationDetailsDto freelancerCertificationDetails) {
        this.freelancerCertificationDetails = freelancerCertificationDetails;
    }
}
