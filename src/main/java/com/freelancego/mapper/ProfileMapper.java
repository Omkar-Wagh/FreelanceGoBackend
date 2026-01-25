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

    PortfolioDto toDto(Portfolio details);
    Portfolio toEntity(PortfolioDto dto);

    List<PortfolioDto> toPortfolioDtoList(List<Portfolio> details);
    List<Portfolio> toPortfolioEntityList(List<PortfolioDto> dtoList);

    CertificationDto toDto(Certification details);
    Certification toEntity(CertificationDto dto);

    List<CertificationDto> toCertificationDtoList(List<Certification> details);
    List<Certification> toCertificationEntityList(List<CertificationDto> dtoList);
}
