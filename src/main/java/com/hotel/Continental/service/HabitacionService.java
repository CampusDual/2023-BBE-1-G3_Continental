package com.hotel.Continental.service;

import com.hotel.Continental.model.Reserva;
import com.hotel.Continental.model.dao.ReservaDao;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.dtoMapper.HabitacionMapper;
import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.api.IHabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.hotel.Continental.model.dao.HabitacionDao;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("HabitacionService")
@Lazy
public class HabitacionService implements IHabitacionService {
   @Autowired
   private HabitacionDao habitacionDao;

   @Autowired
   private ReservaDao reservaDao;

    @Override
    public int insertHabitacion(HabitacionDto habitacionDto) {
        Habitacion habitacion = HabitacionMapper.INSTANCE.toEntity(habitacionDto);
        habitacionDao.saveAndFlush(habitacion);
        return habitacion.getIdHabitacion();
    }

    @Override
    public List<HabitacionDto> getHabitacionesLibres(Date fechaInicio, Date fechaFin) {
        List<Habitacion> habitacionesLibres = habitacionDao.findHabitacionesLibres(fechaInicio, fechaFin);
        List<HabitacionDto> habitacionesDTOLibres = HabitacionMapper.INSTANCE.toDtoList(habitacionesLibres);
        return habitacionesDTOLibres;
    }

    @Override
    public int deleteHabitacion(HabitacionDto habitacionDto) {
        Habitacion habitacion = HabitacionMapper.INSTANCE.toEntity(habitacionDto);
        habitacionDao.delete(habitacion);
        return habitacion.getIdHabitacion();
    }

    @Override
    public HabitacionDto getHabitacionById(int idHabitacion) {
        Habitacion habitacion = habitacionDao.findById(idHabitacion).orElse(null);
        HabitacionDto habitacionDto = HabitacionMapper.INSTANCE.toDto(habitacion);
        return habitacionDto;
    }
}
