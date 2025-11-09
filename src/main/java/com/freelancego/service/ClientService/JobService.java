package com.freelancego.service.ClientService;

import com.freelancego.dto.client.JobDto;
import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.dto.user.ContractDto;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

public interface JobService {

    public JobDto createPost(JobDto jobDto, MultipartFile file,String auth);

    public Page<JobDto> getPostByClient(int page, int size, String email);

    public Map<String,Object> getPostById(int id, String email);

    public Map<String,Object> getDashboardData(String name);

    public List<ContractDto> getPostByPhase(String name);

    public List<JobDto> getPostByStatus(int page, int size, String name);

    Page<ContractDto> getHiredFreelancer(int page, int size, String name);

    Map<String, Object> getAnalytics(String name);

    Page<BidDto> getBids(int jobId, int page, int size,String email);
}

/*

//

status to work on
phase to deal with





 */