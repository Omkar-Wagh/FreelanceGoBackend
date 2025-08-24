package com.freelancego.dto.client;

import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record JobDto(
        @Size(min = 5,message = "Title should be of minimum 5 characters")
        String jobTitle,
        String[] requiredSkills,
        String ExperienceLevel,
        String jobDescription,
        String requirement,
        OffsetDateTime projectStartTime,
        OffsetDateTime projectEndTime,
        @Size(min = 0,message = "Budget should not less or equal to the zero")
        Double budget) {
}
