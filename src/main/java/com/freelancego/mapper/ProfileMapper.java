package com.freelancego.mapper;

import com.freelancego.dto.user.*;
import com.freelancego.model.*;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

    // Map Profile entity to DTO
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "client.id", target = "clientId")
    @Mapping(source = "freelancer.id", target = "freelancerId")
    ProfileDto toDto(Profile profile);

    // Map Profile DTO to entity
    @Mapping(source = "userId", target = "user.id")
    @Mapping(source = "clientId", target = "client.id")
    @Mapping(source = "freelancerId", target = "freelancer.id")
    Profile toEntity(ProfileDto dto);

    // Embedded classes ↔ DTOs
    ProfileDetailsDto toDto(ProfileDetails details);
    ProfileDetails toEntity(ProfileDetailsDto dto);

    PortfolioDetailsDto toDto(PortfolioDetails details);
    PortfolioDetails toEntity(PortfolioDetailsDto dto);

    CertificationDetailsDto toDto(CertificationsDetails details);
    CertificationsDetails toEntity(CertificationDetailsDto dto);
}
