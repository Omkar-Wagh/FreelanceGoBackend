package com.freelancego.mapper;

import com.freelancego.dto.freelancer.FreelancerDto;
import com.freelancego.enums.ExperienceLevel;
import com.freelancego.exception.BadRequestException;
import com.freelancego.model.Freelancer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface FreelancerMapper {

    @Mapping(target = "skills", source = "skills", qualifiedByName = "stringToList")
    @Mapping(target = "experienceLevel", source = "experienceLevel", qualifiedByName = "enumToString")
    @Mapping(target = "userDto", source = "user")
    FreelancerDto toDTO(Freelancer freelancer);

    @Mapping(target = "skills", source = "skills", qualifiedByName = "listToString")
    @Mapping(target = "experienceLevel", source = "experienceLevel", qualifiedByName = "stringToEnum")
    Freelancer toEntity(FreelancerDto freelancerDto);

    @Named("listToString")
    static String mapListToString(List<String> list) {
        return (list == null || list.isEmpty()) ? "" : String.join(",", list);
    }

    @Named("stringToList")
    static List<String> mapStringToList(String skills) {
        return (skills == null || skills.isBlank())
                ? List.of()
                : Arrays.stream(skills.split(","))
                .map(String::trim)
                .collect(Collectors.toList());
    }

    @Named("stringToEnum")
    static ExperienceLevel mapStringToEnum(String level) {
        if (level == null || level.isBlank()) {
            throw new BadRequestException("Experience level is required");
        }
        try {
            return ExperienceLevel.valueOf(level.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid experience level: " + level);
        }
    }

    @Named("enumToString")
    static String mapEnumToString(ExperienceLevel level) {
        return (level == null) ? null : level.name();
    }
}
