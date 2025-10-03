package com.freelancego.mapper;

import com.freelancego.dto.user.ContractDto;
import com.freelancego.enums.ContractStatus;
import com.freelancego.model.Contract;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {BidMapper.class,JobMapper.class,ClientMapper.class,FreelancerMapper.class})
public interface ContractMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "contractStatusToString")
    ContractDto toDTO(Contract contract);

    Contract toEntity(ContractDto contract);

    List<ContractDto> toDtoList(List<Contract> contract);

    @Named("contractStatusToString")
    static String mapContractStatusToString(ContractStatus status) {
        return (status == null) ? null : status.name();
    }
}
