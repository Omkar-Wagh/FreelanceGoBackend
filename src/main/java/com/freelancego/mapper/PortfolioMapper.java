package com.freelancego.mapper;

import com.freelancego.dto.user.PortfolioDto;
import com.freelancego.model.Portfolio;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = {ProfileMapper.class})

public interface PortfolioMapper {

    @Mapping(source = "profile", target = "profileDto",ignore = true)
    PortfolioDto toDto(Portfolio portfolio);

    @Mapping(source = "profileDto", target = "profile",ignore = true)
    Portfolio toEntity(PortfolioDto Dto);
}
