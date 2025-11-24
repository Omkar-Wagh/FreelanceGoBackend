package com.freelancego.service.MilestoneService;

import com.freelancego.dto.user.MilestoneDto;

import java.util.List;

public interface MilestoneService {
    List<MilestoneDto> getMileStone(int contractId, String name);

    MilestoneDto createMilestone(MilestoneDto milestoneDto,int clientId,int freelancerId,String name);
}
