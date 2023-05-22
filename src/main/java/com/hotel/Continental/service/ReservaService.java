package com.hotel.Continental.service;

import com.hotel.Continental.api.IReservaService;
import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.Reserva;
import com.hotel.Continental.model.dao.ReservaDao;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.ReservaDto;
import com.hotel.Continental.model.dto.dtoMapper.HabitacionMapper;
import com.hotel.Continental.model.dto.dtoMapper.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service("ReservaService")
@Lazy
public class ReservaService implements IReservaService {
    @Autowired
    private ReservaDao reservaDao;

    @Override
    public int insertReserva(ReservaDto reservaDto) {
        Reserva reserva = ReservaMapper.INSTANCE.toEntity(reservaDto);
        reservaDao.saveAndFlush(reserva);
        return reserva.getIdReserva();
    }


    @Override
    public List<HabitacionDto> getHabitacionesLibres(Date fechaInicio, Date fechaFin) {
        List<Habitacion> habitacionesLibres = reservaDao.findHabitacionesLibres(fechaInicio, fechaFin);
        List<HabitacionDto> habitacionesDTOLibres = HabitacionMapper.INSTANCE.toDtoList(habitacionesLibres);
        return habitacionesDTOLibres;
    }


}
