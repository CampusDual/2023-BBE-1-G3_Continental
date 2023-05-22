package com.hotel.Continental.api;

import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.dto.ReservaDto;

import java.util.List;

public interface IReservaService {
    int insertReserva(ReservaDto reservaDto);
    Integer queryReservas(List<Habitacion> habitaciones, ReservaDto reservaDto);

}
