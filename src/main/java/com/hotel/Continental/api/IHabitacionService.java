package com.hotel.Continental.api;

import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.dto.HabitacionDto;

import java.util.Date;
import java.util.List;


public interface IHabitacionService {
    int insertHabitacion(HabitacionDto habitacionDto);
    List<HabitacionDto> getHabitacionesLibres(Date fechaInicio, Date fechaFin);
    int deleteHabitacion(HabitacionDto habitacionDto);
    HabitacionDto getHabitacionById(int idHabitacion);


}
