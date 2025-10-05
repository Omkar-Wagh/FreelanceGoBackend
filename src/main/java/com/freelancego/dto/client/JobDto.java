package com.freelancego.dto.client;

import com.freelancego.dto.freelancer.BidDto;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.OffsetDateTime;
import java.util.List;

public record JobDto(

        int id,

        @NotBlank(message = "Job title cannot be blank")
        @Size(min = 5, message = "Title should be of minimum 5 characters")
        String jobTitle,

        @NotNull(message = "Skills cannot be null")
        List<String> requiredSkills,

        @NotBlank(message = "Experience level is required")
        String experienceLevel,

        @NotBlank(message = "Job description cannot be blank")
        String jobDescription,

        @NotBlank(message = "Requirement cannot be blank")
        String requirement,

        @NotNull(message = "Project start time is required")
        OffsetDateTime projectStartTime,

        @NotNull(message = "Project end time is required")
        OffsetDateTime projectEndTime,

        OffsetDateTime createdAt,

        @DecimalMin(value = "0.1", message = "Budget must be greater than zero")
        @NotNull(message = "Budget is required")
        long budget,

        String status,

        String phase,

        ClientDto clientDto,

        List<BidDto> bidDto,

        boolean alreadyBid
) { }
