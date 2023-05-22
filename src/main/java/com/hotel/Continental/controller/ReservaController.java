package com.hotel.Continental.controller;

import com.hotel.Continental.api.IHabitacionService;
import com.hotel.Continental.api.IReservaService;
import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.ReservaDto;
import com.hotel.Continental.model.dto.dtoMapper.HabitacionMapper;
import com.hotel.Continental.service.ReservaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/reserva")
public class ReservaController {
    @Autowired
    private IReservaService reservaService;
    @Autowired
    private IHabitacionService habitacionService;

    @PostMapping(value = "/add")
    public int addReserva(@RequestBody ReservaDto reservaDto) {
        if (reservaDto.getIdHabitacion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IdHabitacion is required");
        } else if (reservaDto.getFechaInicio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FechaInicio is required");
        } else if (reservaDto.getFechaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FechaFin is required");
        }
        return reservaService.insertReserva(reservaDto);
    }
    @PostMapping(value = "/getHabitacionesLibres")
    public List<HabitacionDto> getHabitacionesLibres(@RequestBody ReservaDto reservaDto) {
        if (reservaDto.getFechaInicio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FechaInicio is required");
        } else if (reservaDto.getFechaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FechaFin is required");
        }
        List<HabitacionDto> habitacionesLibres = reservaService.getHabitacionesLibres(reservaDto.getFechaInicio(), reservaDto.getFechaFin());
        return habitacionesLibres;
    }
}
