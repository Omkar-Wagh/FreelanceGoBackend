package com.freelancego.mapper;

import com.freelancego.dto.user.SubmissionDto;
import com.freelancego.enums.SubmissionStatus;
import com.freelancego.model.Submission;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
@Mapper(componentModel = "spring")
public interface SubmissionMapper {
    @Mapping(target = "status", source = "status", qualifiedByName = "submissionStatusToString")
    SubmissionDto toDTO(Submission submission);

    Submission toEntity(SubmissionDto submissionDto);

    List<SubmissionDto> toDtoList(List<Submission> Submission);

    @Named("submissionStatusToString")
    static String mapSubmissionStatusToString(SubmissionStatus status) {
        return (status == null) ? null : status.name();
    }
}