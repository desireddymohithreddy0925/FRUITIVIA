package com.fruitivia.buyer.dto;

import com.fruitivia.buyer.Buyer;
import com.fruitivia.user.dto.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BuyerMapper {
    BuyerDto toDto(Buyer buyer);
    Buyer toEntity(BuyerDto dto);
}
