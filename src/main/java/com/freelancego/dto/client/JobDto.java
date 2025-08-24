package com.freelancego.dto.client;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;

public record JobDto(
        @Size(min = 5,message = "Title should be of minimum 5 characters")
        String jobTitle,
        String[] requiredSkills,
        String experienceLevel,
        String jobDescription,
        String requirement,
        OffsetDateTime projectStartTime,
        OffsetDateTime projectEndTime,
        @DecimalMin(value = "0.1", message = "Budget must be greater than zero")
        Double budget) {
}
