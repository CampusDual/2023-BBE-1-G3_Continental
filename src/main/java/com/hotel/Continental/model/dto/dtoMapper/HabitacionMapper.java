package com.hotel.Continental.model.dto.dtoMapper;

import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.Habitacion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HabitacionMapper {
    HabitacionMapper INSTANCE = Mappers.getMapper(HabitacionMapper.class);
    @Mapping(source = "hotel", target = "idHotel",qualifiedByName = "hotelToIdHotel")
    HabitacionDto toDto(Habitacion habitacion);
    @Mapping(source = "idHotel", target = "hotel",qualifiedByName = "idHotelToHotel")
    Habitacion toEntity(HabitacionDto habitacionDto);
    List<HabitacionDto> toDtoList(List<Habitacion> habitaciones);
    List<Habitacion> toEntityList(List<HabitacionDto> habitacionDtos);
    @Named("idHotelToHotel")
    default Hotel idHotelToHotel(int idHotel){
        return new Hotel(idHotel);
    }
    @Named("hotelToIdHotel")
    default int hotelToIdHotel(Hotel hotel){
        return hotel.getId();
    }
}
