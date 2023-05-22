package com.hotel.Continental.api;

import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.ReservaDto;

import java.util.List;

public interface IReservaService {
    int insertReserva(ReservaDto reservaDto);
    List<ReservaDto> queryReservas(List<HabitacionDto> habitaciones, ReservaDto reservaDto);

}
