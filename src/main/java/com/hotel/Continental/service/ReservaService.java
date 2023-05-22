package com.hotel.Continental.service;

import com.hotel.Continental.api.IReservaService;
import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.Reserva;
import com.hotel.Continental.model.dao.HabitacionDao;
import com.hotel.Continental.model.dao.ReservaDao;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.ReservaDto;
import com.hotel.Continental.model.dto.dtoMapper.HabitacionMapper;
import com.hotel.Continental.model.dto.dtoMapper.ReservaMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("ReservaService")
@Lazy
public class ReservaService implements IReservaService {
    @Autowired
    private ReservaDao reservaDao;
    @Autowired
    private HabitacionDao habitacionDao;

    @Override
    public int insertReserva(ReservaDto reservaDto) {
        HabitacionService habitacionService = new HabitacionService();
            List<Habitacion> listaHabitacionesHotel = habitacionService.queryHotelHabitacion(reservaDto.getIdHotel());
            Habitacion habitacion = new Habitacion(queryReservas(listaHabitacionesHotel, reservaDto));
            Reserva reserva = ReservaMapper.INSTANCE.toEntity(reservaDto);
            reserva.setHabitacion(habitacion);
            reservaDao.saveAndFlush(reserva);

            return reserva.getIdReserva();
    }

    //Hay que comparar la fecha de la lista de todas las reservas a la de la reserva nueva
    // Y optimizarlo
    @Override
    public Integer queryReservas(List<Habitacion> habitaciones, ReservaDto reservaDto) {
        List<ReservaDto> reservasDto = ReservaMapper.INSTANCE.toDtoList(reservaDao.findAll());
        List<Reserva> reservas = ReservaMapper.INSTANCE.toEntityList(reservasDto);

        for (Habitacion h : habitaciones) {
            for (Reserva r : reservas) {
                if (h.getIdHabitacion() == r.getHabitacion().getIdHabitacion()) {
                    if (reservaDto.getFechaInicio().after(r.getFechaInicio()) && reservaDto.getFechaFin().before(r.getFechaFin())) {
                        return h.getIdHabitacion();
                    }
                }
            }
        }
        return null;
    }
}
