package com.freelancego.mapper;

import com.freelancego.dto.user.MilestoneDto;
import com.freelancego.enums.MilestoneStatus;
import com.freelancego.enums.PaymentStatus;
import com.freelancego.enums.VerificationStatus;
import com.freelancego.model.Milestone;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
@Mapper(componentModel = "spring", uses = {ContractMapper.class})
public interface MilestoneMapper {
    @Mapping(target = "status", source = "status", qualifiedByName = "milestoneStatusToString")
    @Mapping(target = "paymentStatus", source = "paymentStatus", qualifiedByName = "paymentStatusToString")
    @Mapping(target = "verificationStatus", source = "verificationStatus", qualifiedByName = "verificationStatusToString")
    MilestoneDto toDTO(Milestone Milestone);

    Milestone toEntity(MilestoneDto Milestone);

    List<MilestoneDto> toDtoList(List<Milestone> Milestone);

    @Named("milestoneStatusToString")
    static String mapMilestoneStatusToString(MilestoneStatus status) {
        return (status == null) ? null : status.name();
    }
    @Named("paymentStatusToString")
    static String mapPaymentStatusToString(PaymentStatus status) {
        return (status == null) ? null : status.name();
    }
    @Named("verificationStatusToString")
    static String mapVerificationStatusToString(VerificationStatus status) {
        return (status == null) ? null : status.name();
    }
}
