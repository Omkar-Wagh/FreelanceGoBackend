package com.freelancego.dto.user;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.dto.freelancer.FreelancerDto;

public class ProfileDto {
    private int id;
    private UserDto user;
    private ClientDto client;
    private FreelancerDto freelancer;

    private ProfileDetailsDto clientProfile;
    private ProfileDetailsDto freelancerProfile;

    private PortfolioDetailsDto clientPortfolioDetails;
    private PortfolioDetailsDto freelancerPortfolioDetails;

    private CertificationDetailsDto clientCertificationDetails;
    private CertificationDetailsDto freelancerCertificationDetails;

    private boolean isOwnProfile;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public UserDto getUser() {
        return user;
    }

    public void setUser(UserDto user) {
        this.user = user;
    }

    public ClientDto getClient() {
        return client;
    }

    public void setClient(ClientDto client) {
        this.client = client;
    }

    public FreelancerDto getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(FreelancerDto freelancer) {
        this.freelancer = freelancer;
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

    public boolean isOwnProfile() {
        return isOwnProfile;
    }

    public void setOwnProfile(boolean ownProfile) {
        isOwnProfile = ownProfile;
    }
}
