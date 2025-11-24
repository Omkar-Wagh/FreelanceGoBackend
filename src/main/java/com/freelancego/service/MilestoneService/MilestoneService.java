package com.freelancego.service.MilestoneService;

import com.freelancego.dto.user.MilestoneDto;

import java.util.List;

public interface MilestoneService {
    List<MilestoneDto> getMileStone(int contractId, String name);

    MilestoneDto createMilestone(MilestoneDto milestoneDto,int clientId,int freelancerId,String name);

    MilestoneDto updateMilestone(MilestoneDto milestoneDto, int freelancerId, String name);

    MilestoneDto editMilestone(MilestoneDto milestoneDto, int clientId, String name);

    MilestoneDto approveMilestone(int milestoneId, int clientId, String name);
}
