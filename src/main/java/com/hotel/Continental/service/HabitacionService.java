package com.hotel.Continental.service;

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

    @Override
    public int insertHabitacion(HabitacionDto habitacionDto) {
        Habitacion habitacion = HabitacionMapper.INSTANCE.toEntity(habitacionDto);
        habitacionDao.saveAndFlush(habitacion);
        return habitacion.getIdHabitacion();
    }

    @Override
    public List<Habitacion> queryHotelHabitacion(int idHotel) {
        List<HabitacionDto> lista = findAll().stream().filter(h -> h.getIdHotel() == idHotel).collect(Collectors.toList());
        List<Habitacion> listaHabitacion = HabitacionMapper.INSTANCE.toEntityList(lista);
        return listaHabitacion;
    }

    @Override
    public List<HabitacionDto> findAll() {
        return HabitacionMapper.INSTANCE.toDtoList(habitacionDao.findAll());
    }

    @Override
    public List<HabitacionDto> getHabitacionesLibres(Date fechaInicio, Date fechaFin) {
        List<Habitacion> habitacionesLibres = habitacionDao.findHabitacionesLibres(fechaInicio, fechaFin);
        List<HabitacionDto> habitacionesDTOLibres = HabitacionMapper.INSTANCE.toDtoList(habitacionesLibres);
        return habitacionesDTOLibres;
    }
}
