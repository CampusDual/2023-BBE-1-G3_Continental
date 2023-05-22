package com.hotel.Continental.model.dto.dtoMapper;

import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.Reserva;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.ReservaDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper
public interface ReservaMapper {
    ReservaMapper INSTANCE = Mappers.getMapper(ReservaMapper.class);
    @Mapping(source = "habitacion", target = "idHabitacion", qualifiedByName = "habitacionToIdHabitacion")
    ReservaDto toDto(Reserva reserva);
    @Mapping(source = "idHabitacion", target = "habitacion",qualifiedByName = "IdHabitacionToHabitacion")
    Reserva toEntity(ReservaDto reservaDto);
    List<ReservaDto> toDtoList(List<Reserva> reservas);
    List<Reserva> toEntityList(List<ReservaDto> reservasdtos);
    @Named("idHabitacionToHabitacion")
    default Habitacion idHabitacionToHabitacion(int idHabitacion){
        return new Habitacion(idHabitacion);
    }
    @Named("hotelToIdHotel")
    default int habitacionToidHabitacion(Habitacion habitacion){
        return habitacion.getIdHabitacion();
    }
    @Named("IdHabitacionToHabitacion")
    default Habitacion idHotelToHabitacion(int idHotel){
        return new Habitacion(idHotel);
    }
    @Named("habitacionToIdHabitacion")
    default int habitacionToidHotel(Habitacion habitacion){
        return habitacion.getHotel().getId();
    }
}
