package com.freelancego.dto.freelancer;

import com.freelancego.dto.client.JobDto;

public class BrowseJobDto {

    private JobDto job;
    private boolean alreadyBid;
    private boolean isOwnPost;

    public JobDto getJob() {
        return job;
    }

    public void setJob(JobDto job) {
        this.job = job;
    }

    public boolean isAlreadyBid() {
        return alreadyBid;
    }

    public void setAlreadyBid(boolean alreadyBid) {
        this.alreadyBid = alreadyBid;
    }

    public boolean isOwnPost() {
        return isOwnPost;
    }

    public void setOwnPost(boolean ownPost) {
        isOwnPost = ownPost;
    }
}