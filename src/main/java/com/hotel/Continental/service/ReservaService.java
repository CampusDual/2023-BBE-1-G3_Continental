package com.hotel.Continental.service;

import com.hotel.Continental.api.IReservaService;
import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.Reserva;
import com.hotel.Continental.model.dao.HabitacionDao;
import com.hotel.Continental.model.dao.ReservaDao;
import com.hotel.Continental.model.dto.ReservaDto;
import com.hotel.Continental.model.dto.dtoMapper.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import java.util.stream.Stream;

@Service("ReservaService")
@Lazy
public class ReservaService implements IReservaService {
    @Autowired
    private ReservaDao reservaDao;
    @Autowired
    private HabitacionDao habitacionDao;

    @Override
    public int deleteReserva(ReservaDto reservaDto) {
        Reserva reserva = ReservaMapper.INSTANCE.toEntity(reservaDto);
        reservaDao.delete(reserva);
        return reserva.getIdReserva();
    }

    @Override
    public int insertReserva(ReservaDto reservaDto) {
        Reserva reserva = ReservaMapper.INSTANCE.toEntity(reservaDto);
        Stream<Habitacion> habitacionesLibres = habitacionDao.findHabitacionesLibres(reserva.getFechaInicio(), reserva.getFechaFin()).stream();
        if (habitacionesLibres.anyMatch(h -> h.getIdHabitacion() == reservaDto.getIdHabitacion())) {
            reservaDao.saveAndFlush(reserva);
            return reserva.getIdReserva();
        }
        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "La habitacion está reservada");
    }
}
