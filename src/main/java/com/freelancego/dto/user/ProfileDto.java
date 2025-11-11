package com.freelancego.dto.user;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.dto.freelancer.FreelancerDto;
import java.util.List;

public class ProfileDto {
    private int id;
    private UserDto user;
    private ClientDto client;
    private FreelancerDto freelancer;

    private ProfileDetailsDto clientProfile;
    private ProfileDetailsDto freelancerProfile;

    private List<PortfolioDetailsDto> clientPortfolioDetails;
    private List<PortfolioDetailsDto> freelancerPortfolioDetails;

    private List<CertificationDetailsDto> clientCertificationDetails;
    private List<CertificationDetailsDto> freelancerCertificationDetails;

    private boolean ownProfile;

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

    public List<PortfolioDetailsDto> getClientPortfolioDetails() {
        return clientPortfolioDetails;
    }

    public void setClientPortfolioDetails(List<PortfolioDetailsDto> clientPortfolioDetails) {
        this.clientPortfolioDetails = clientPortfolioDetails;
    }

    public List<PortfolioDetailsDto> getFreelancerPortfolioDetails() {
        return freelancerPortfolioDetails;
    }

    public void setFreelancerPortfolioDetails(List<PortfolioDetailsDto> freelancerPortfolioDetails) {
        this.freelancerPortfolioDetails = freelancerPortfolioDetails;
    }

    public List<CertificationDetailsDto> getClientCertificationDetails() {
        return clientCertificationDetails;
    }

    public void setClientCertificationDetails(List<CertificationDetailsDto> clientCertificationDetails) {
        this.clientCertificationDetails = clientCertificationDetails;
    }

    public List<CertificationDetailsDto> getFreelancerCertificationDetails() {
        return freelancerCertificationDetails;
    }

    public void setFreelancerCertificationDetails(List<CertificationDetailsDto> freelancerCertificationDetails) {
        this.freelancerCertificationDetails = freelancerCertificationDetails;
    }

    public boolean isOwnProfile() {
        return ownProfile;
    }

    public void setOwnProfile(boolean ownProfile) {
        this.ownProfile = ownProfile;
    }
}
