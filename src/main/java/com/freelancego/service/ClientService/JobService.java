package com.freelancego.service.ClientService;

import com.freelancego.dto.client.JobDto;
import java.util.*;

public interface JobService {

    public JobDto createPost(JobDto jobDto,String auth);

    public List<JobDto> getPostByClient(String email);

    public JobDto getPostById(int id, String email);

}