package com.freelancego.service.ContractService;

import com.freelancego.dto.user.ContractDto;

public interface ContractService {
    ContractDto getContract(int jobId, int freelancerId, int clientId,String name);

    ContractDto createContract(int bidId, int jobId, int freelancerId, int clientId, String name);
}
