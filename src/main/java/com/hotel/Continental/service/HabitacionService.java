package com.hotel.Continental.service;

import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.dtoMapper.HabitacionMapper;
import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.dao.HabitacionDao;
import com.hotel.Continental.api.IHabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

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
}
