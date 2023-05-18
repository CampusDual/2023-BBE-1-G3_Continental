package com.hotel.Continental.model.dto.dtoMapper;

import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dto.HotelDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;
@Mapper
public interface HotelMapper {
    HotelMapper INSTANCE = Mappers.getMapper(HotelMapper.class);
    HotelDTO toDTO(Hotel hotel);
    Hotel toEntity(HotelDTO hotelDto);
    List<HotelDTO> toDtoList(List<Hotel> hotels);
    List <Hotel> toEntityList(List<HotelDTO> hotelDtos);
}
