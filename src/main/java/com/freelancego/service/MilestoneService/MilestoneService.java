package com.freelancego.service.MilestoneService;

import com.freelancego.dto.user.MilestoneDto;
import com.freelancego.dto.user.SubmissionDto;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MilestoneService {
    List<MilestoneDto> getMileStone(int contractId, String name);

    MilestoneDto createMilestone(MilestoneDto milestoneDto,int clientId,int freelancerId,String name);

    MilestoneDto updateMilestone(MilestoneDto milestoneDto, int freelancerId, String name);

    MilestoneDto editMilestone(MilestoneDto milestoneDto, int clientId, String name);

    MilestoneDto approveMilestone(int milestoneId, int clientId, String name);

    SubmissionDto getSubmission(int milestoneId, int clientId, String name);

    SubmissionDto createSubmission(SubmissionDto submissionDto, MultipartFile file, int milestoneId, int freelancerId, String name);

    SubmissionDto updateSubmission(SubmissionDto submissionDto, MultipartFile file, int milestoneId, int freelancerId, String name);

    SubmissionDto editSubmission(SubmissionDto submissionDto, int clientId, String name);

    MilestoneDto approveSubmission(int submissionId, int clientId, String name);
}
