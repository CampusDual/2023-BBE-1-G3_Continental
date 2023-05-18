package com.hotel.Continental.Model.Dto.mapper;

import com.hotel.Continental.Model.Dto.HabitacionDto;
import com.hotel.Continental.Model.Habitacion;
import org.mapstruct.factory.Mappers;

import java.util.List;

public interface HabitacionMapper {
    HabitacionMapper INSTANCE = Mappers.getMapper(HabitacionMapper.class);
    HabitacionDto toDto(Habitacion habitacion);
    Habitacion toEntity(HabitacionDto habitacionDto);
    List<HabitacionDto> toDtoList(List<Habitacion> habitaciones);
    List<Habitacion> toEntityList(List<HabitacionDto> habitacionDtos);
}
