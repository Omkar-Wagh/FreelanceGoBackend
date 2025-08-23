package com.freelancego.dto.client;

import com.freelancego.enums.Role;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.Size;

import java.time.ZoneOffset;

public record JobDto(
        @Size(min = 5,message = "Title should be of minimum 5 characters")
        String jobTitle,
        String[] requiredSkills,
        String ExperienceLevel,
        String jobDescription,
        String requirement,
        ZoneOffset projectStartTime,
        ZoneOffset projectEndTime,
        @Size(min = 0,message = "Budget should not less or equal to the zero")
        Double budget) {
}
