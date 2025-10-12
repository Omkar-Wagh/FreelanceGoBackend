package com.freelancego.model;

import com.freelancego.enums.ExperienceLevel;
import com.freelancego.enums.JobPhase;
import com.freelancego.enums.JobStatus;
import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Job {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private String jobTitle;
    private String requiredSkills;

    @Enumerated(EnumType.STRING)
    private ExperienceLevel experienceLevel;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String jobDescription;

    @Lob
    @Column(columnDefinition = "TEXT")
    private String requirement;

    private String category;
    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime projectStartTime;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE")
    private OffsetDateTime projectEndTime;

    @Column(columnDefinition = "TIMESTAMP WITH TIME ZONE", updatable = false, nullable = false)
    private OffsetDateTime createdAt;

    private long budget;

    private String file;

    @Enumerated(EnumType.STRING)
    private JobStatus status;

    @Enumerated(EnumType.STRING)
    private JobPhase phase;

    @ManyToOne
    private Client client;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL)
    private List<Bid> bids = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = OffsetDateTime.now();
    }

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

    public ExperienceLevel getExperienceLevel() {
        return experienceLevel;
    }

    public void setExperienceLevel(ExperienceLevel experienceLevel) {
        this.experienceLevel = experienceLevel;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public OffsetDateTime getProjectStartTime() {
        return projectStartTime;
    }

    public void setProjectStartTime(OffsetDateTime projectStartTime) {
        this.projectStartTime = projectStartTime;
    }

    public OffsetDateTime getProjectEndTime() {
        return projectEndTime;
    }

    public void setProjectEndTime(OffsetDateTime projectEndTime) {
        this.projectEndTime = projectEndTime;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public long getBudget() {
        return budget;
    }

    public void setBudget(long budget) {
        this.budget = budget;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public JobStatus getStatus() {
        return status;
    }

    public void setStatus(JobStatus status) {
        this.status = status;
    }

    public JobPhase getPhase() {
        return phase;
    }

    public void setPhase(JobPhase phase) {
        this.phase = phase;
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
