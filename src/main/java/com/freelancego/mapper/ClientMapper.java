package com.freelancego.mapper;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.model.Client;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ClientMapper{

    @Mapping(target = "userDto", source = "user")  // will delegate to UserMapper
    ClientDto toDTO(Client client);
    Client toEntity(ClientDto clientDto);
}