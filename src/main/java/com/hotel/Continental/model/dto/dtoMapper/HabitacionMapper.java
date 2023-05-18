package com.hotel.Continental.model.dto.dtoMapper;

import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.Habitacion;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface HabitacionMapper {
    HabitacionMapper INSTANCE = Mappers.getMapper(HabitacionMapper.class);
    HabitacionDto toDto(Habitacion habitacion);
    Habitacion toEntity(HabitacionDto habitacionDto);
    List<HabitacionDto> toDtoList(List<Habitacion> habitaciones);
    List<Habitacion> toEntityList(List<HabitacionDto> habitacionDtos);
}
