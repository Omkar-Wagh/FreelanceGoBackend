package com.freelancego.mapper;

import com.freelancego.dto.user.CertificationDto;
import com.freelancego.model.Certification;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class})

public interface CertificationMapper {

    @Mapping(source = "profile", target = "profileDto",ignore = true)
    CertificationDto toDto(Certification certification);

    @Mapping(source = "profileDto", target = "profile",ignore = true)
    Certification toEntity(CertificationDto Dto);

    List<CertificationDto> toDtoList(List<Certification> certifications);

}

