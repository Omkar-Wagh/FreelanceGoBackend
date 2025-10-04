package com.freelancego.service.ClientService;

import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.user.ContractDto;
import org.springframework.data.domain.Page;

import java.util.*;

public interface JobService {

    public JobDto createPost(JobDto jobDto,String auth);

    public Page<JobDto> getPostByClient(int page, int size, String email);

    public Map<String,Object> getPostById(int id, String email);

    public Map<String,Object> getDashboardData(String name);

    public List<ContractDto> getPostByPhase(String name);

    public Page<JobDto> getPostByStatus(int page, int size, String name);

    Page<ContractDto> getHiredFreelancer(int page, int size, String name);
}

/*

//

status to work on
phase to deal with





 */