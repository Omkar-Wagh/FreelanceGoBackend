package com.freelancego.model;

import com.freelancego.enums.Role;
import jakarta.persistence.*;
import java.time.ZoneOffset;
import java.util.List;

@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String jobTitle;
    private String requiredSkills;
    private Role ExperienceLevel;
    private String jobDescription;
    @Lob
    private String requirement;
    private ZoneOffset projectStartTime;
    private ZoneOffset projectEndTime;
    private Double budget;
    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "job")
    private List<Bid> bids;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getRequiredSkills() {
        return requiredSkills;
    }

    public void setRequiredSkills(String requiredSkills) {
        this.requiredSkills = requiredSkills;
    }

    public Role getExperienceLevel() {
        return ExperienceLevel;
    }

    public void setExperienceLevel(Role experienceLevel) {
        ExperienceLevel = experienceLevel;
    }

    public String getJobDescription() {
        return jobDescription;
    }

    public void setJobDescription(String jobDescription) {
        this.jobDescription = jobDescription;
    }

    public String getRequirement() {
        return requirement;
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
    }

    public ZoneOffset getProjectStartTime() {
        return projectStartTime;
    }

    public void setProjectStartTime(ZoneOffset projectStartTime) {
        this.projectStartTime = projectStartTime;
    }

    public ZoneOffset getProjectEndTime() {
        return projectEndTime;
    }

    public void setProjectEndTime(ZoneOffset projectEndTime) {
        this.projectEndTime = projectEndTime;
    }

    public Double getBudget() {
        return budget;
    }

    public void setBudget(Double budget) {
        this.budget = budget;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public List<Bid> getBids() {
        return bids;
    }

    public void setBids(List<Bid> bids) {
        this.bids = bids;
    }
}
