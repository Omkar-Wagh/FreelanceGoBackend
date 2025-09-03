package com.freelancego.mapper;

import com.freelancego.dto.freelancer.BidDto;
import com.freelancego.model.Bid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {JobMapper.class, FreelancerMapper.class})
public interface BidMapper {

    @Mapping(source = "freelancer", target = "freelancerDto")
    @Mapping(source = "job", target = "jobDto", ignore = true) // 👈 don’t map job into dto
    BidDto toDto(Bid bid);

    @Mapping(source = "freelancerDto", target = "freelancer")
    @Mapping(source = "jobDto", target = "job")   // 👈 allow job mapping from dto to entity
    Bid toEntity(BidDto bidDto);

    List<BidDto> toDtoList(List<Bid> bids);

}
