package com.hotel.Continental.api;

import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.dto.HabitacionDto;

import java.util.Date;
import java.util.List;


public interface IHabitacionService {
    int insertHabitacion(HabitacionDto habitacionDto);
    public List<Habitacion> queryHotelHabitacion(int idHotel);
    public List<HabitacionDto> findAll();
    List<HabitacionDto> getHabitacionesLibres(Date fechaInicio, Date fechaFin);



}
