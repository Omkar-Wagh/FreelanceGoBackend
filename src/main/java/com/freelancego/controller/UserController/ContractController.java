package com.freelancego.controller.UserController;

import com.freelancego.dto.user.ContractDto;
import com.freelancego.service.ContractService.ContractService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/")
public class ContractController {

    private final ContractService contractService;

    public ContractController(ContractService contractService) {
        this.contractService = contractService;
    }


    @GetMapping("/contract")
    ResponseEntity<String> getContract(@PathVariable("jobId") int jobId, @PathVariable("freelancerId") int freelancerId,
                                       @PathVariable("clientId") int clientId, Authentication auth){
        contractService.getContract(jobId,freelancerId,clientId,auth.getName());
        return ResponseEntity.ok("Contract");
    }

    @PostMapping("/create-contract/{bidId}/{jobId}/{clientId}/{freelancerId}/")
    ResponseEntity<ContractDto> createContract(@PathVariable int bidId,@PathVariable("jobId") int jobId, @PathVariable("freelancerId") int freelancerId,
                                               @PathVariable("clientId") int clientId,Authentication auth){
        return ResponseEntity.ok(contractService.createContract(bidId,jobId,freelancerId,clientId,auth.getName());
    }
}
