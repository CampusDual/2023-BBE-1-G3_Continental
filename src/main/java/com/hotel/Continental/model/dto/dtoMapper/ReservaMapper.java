package com.hotel.Continental.model.dto.dtoMapper;

import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.Reserva;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.ReservaDto;
import com.hotel.Continental.service.HabitacionService;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ReservaMapper {
    ReservaMapper INSTANCE = Mappers.getMapper(ReservaMapper.class);
    @Mapping(source = "habitacion", target = "idHotel", qualifiedByName = "habitacionToIdHotel")
    ReservaDto toDto(Reserva reserva);
    @Mapping(source = "idHotel", target = "habitacion",qualifiedByName = "idHotelToHabitacion")
    Reserva toEntity(ReservaDto reservaDto);
    List<ReservaDto> toDtoList(List<Reserva> reservas);
    List<Reserva> toEntityList(List<ReservaDto> reservasdtos);
    @Named("idHotelToHabitacion")
    default Habitacion idHotelToHabitacion(int idHotel){
        return new Habitacion(idHotel);
    }
    @Named("habitacionToIdHotel")
    default int habitacionToidHotel(Habitacion habitacion){
        return habitacion.getHotel().getId();
    }
}
