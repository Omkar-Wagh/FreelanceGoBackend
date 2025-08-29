package com.freelancego.mapper;

import com.freelancego.dto.client.JobDto;
import com.freelancego.enums.ExperienceLevel;
import com.freelancego.enums.JobPostStatus;
import com.freelancego.exception.BadRequestException;
import com.freelancego.model.Job;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface JobMapper {

    @Mapping(target = "requiredSkills", source = "requiredSkills", qualifiedByName = "listToString")
    @Mapping(target = "experienceLevel", source = "experienceLevel", qualifiedByName = "stringToExperienceLevel")
    @Mapping(target = "status", source = "status", qualifiedByName = "stringToJobPostStatus")
    Job toEntity(JobDto dto);

    @Mapping(target = "requiredSkills", source = "requiredSkills", qualifiedByName = "stringToList")
    @Mapping(target = "experienceLevel", source = "experienceLevel", qualifiedByName = "experienceLevelToString")
    @Mapping(target = "status", source = "status", qualifiedByName = "jobPostStatusToString")
    JobDto toDto(Job job);

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

    // 🔹 String ↔ ExperienceLevel
    @Named("stringToExperienceLevel")
    static ExperienceLevel mapStringToExperienceLevel(String level) {
        if (level == null || level.isBlank()) {
            throw new BadRequestException("Experience level is required");
        }
        try {
            return ExperienceLevel.valueOf(level.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid experience level: " + level);
        }
    }

    @Named("experienceLevelToString")
    static String mapExperienceLevelToString(ExperienceLevel level) {
        return (level == null) ? null : level.name();
    }

    @Named("stringToJobPostStatus")
    static JobPostStatus mapStringToJobPostStatus(String status) {
        if (status == null || status.isBlank()) {
            throw new BadRequestException("Status is required");
        }
        try {
            return JobPostStatus.valueOf(status.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid status: " + status);
        }
    }

    @Named("jobPostStatusToString")
    static String mapJobPostStatusToString(JobPostStatus status) {
        return (status == null) ? null : status.name();
    }
}
