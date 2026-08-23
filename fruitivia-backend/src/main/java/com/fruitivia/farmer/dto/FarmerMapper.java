package com.fruitivia.farmer.dto;

import com.fruitivia.farmer.Farmer;
import com.fruitivia.user.dto.UserMapper;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", uses = {UserMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface FarmerMapper {
    FarmerDto toDto(Farmer farmer);
    Farmer toEntity(FarmerDto dto);
}
