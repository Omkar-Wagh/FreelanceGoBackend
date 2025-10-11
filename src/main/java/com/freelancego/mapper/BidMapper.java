package com.freelancego.mapper;

import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.enums.BidStatus;
import com.freelancego.model.Bid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;

@Mapper(componentModel = "spring", uses = {JobMapper.class, FreelancerMapper.class})
public interface BidMapper {

    @Mapping(target = "status", source = "status", qualifiedByName = "bidStatusToString")
    @Mapping(source = "freelancer", target = "freelancerDto")
    @Mapping(source = "job", target = "jobDto",ignore = true) // 👈 don’t map job into dto
    BidDto toDto(Bid bid);

    @Mapping(source = "freelancerDto", target = "freelancer")
    @Mapping(source = "jobDto", target = "job")   // 👈 allow job mapping from dto to entity
    Bid toEntity(BidDto bidDto);

    List<BidDto> toDtoList(List<Bid> bids);

    @Named("bidStatusToString")
    static String mapContractStatusToString(BidStatus status) {
        return (status == null) ? null : status.name();
    }

    default Bid toMapEntity(BidDto bidDto) {

        if ( bidDto == null ) {
            return null;
        }

        Bid bid = new Bid();

        bid.setId( bidDto.id() );
        bid.setFreelancer(null);
        bid.setJob(null);
        bid.setAmount( bidDto.amount() );
        bid.setCoverLetter( bidDto.coverLetter() );
        bid.setTimeRequired( bidDto.timeRequired() );
        return bid;
    }
}