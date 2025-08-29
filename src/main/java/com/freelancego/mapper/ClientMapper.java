package com.freelancego.mapper;

import com.freelancego.dto.client.ClientDto;
import com.freelancego.model.Client;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ClientMapper{
    ClientDto toDTO(Client client);
    Client toEntity(ClientDto clientDto);
}
