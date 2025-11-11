package com.freelancego.mapper;

import com.freelancego.dto.user.*;
import com.freelancego.model.*;
import org.mapstruct.Mapper;
import java.util.List;

@Mapper(componentModel = "spring", uses = {UserMapper.class, FreelancerMapper.class, ClientMapper.class})
public interface ProfileMapper {

    ProfileDto toDto(Profile profile);
    Profile toEntity(ProfileDto dto);

    ProfileDetailsDto toDto(ProfileDetails details);
    ProfileDetails toEntity(ProfileDetailsDto dto);

    PortfolioDetailsDto toDto(PortfolioDetails details);
    PortfolioDetails toEntity(PortfolioDetailsDto dto);

    List<PortfolioDetailsDto> toPortfolioDtoList(List<PortfolioDetails> details);
    List<PortfolioDetails> toPortfolioEntityList(List<PortfolioDetailsDto> dtoList);

    CertificationDetailsDto toDto(CertificationsDetails details);
    CertificationsDetails toEntity(CertificationDetailsDto dto);

    List<CertificationDetailsDto> toCertificationDtoList(List<CertificationsDetails> details);
    List<CertificationsDetails> toCertificationEntityList(List<CertificationDetailsDto> dtoList);
}
