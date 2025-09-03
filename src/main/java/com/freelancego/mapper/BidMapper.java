package com.freelancego.mapper;

import com.freelancego.dto.client.BidDto;
import com.freelancego.model.Bid;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import java.util.List;

@Mapper(componentModel = "spring", uses = {JobMapper.class, FreelancerMapper.class})
public interface BidMapper {

    @Mapping(source = "job", target = "jobDto")
    @Mapping(source = "freelancer", target = "freelancerDto")
    BidDto toDto(Bid bid);

    @Mapping(source = "jobDto", target = "job")
    @Mapping(source = "freelancerDto", target = "freelancer")
    Bid toEntity(BidDto bidDto);

    List<BidDto> toDtoList(List<Bid> bids);

}
