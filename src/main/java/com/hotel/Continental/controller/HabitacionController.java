package com.hotel.Continental.controller;

import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.api.IHabitacionService;
import com.hotel.Continental.model.dto.ReservaDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/habitacion")
public class HabitacionController {

    @Autowired
    private IHabitacionService habitacionService;

    @PostMapping(value = "/add")
    public int addHabitacion(@RequestBody HabitacionDto habitacionDto) {
        if (habitacionDto.getIdHotel() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IdHotel is required");
        } else if (habitacionDto.getNumHabitacion() == 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "NumHabitacion is required");
        }
        return habitacionService.insertHabitacion(habitacionDto);
    }

    @PostMapping(value = "/getHabitacionesLibres")
    public List<HabitacionDto> getHabitacionesLibres(@RequestBody ReservaDto reservaDto) {
        if (reservaDto.getFechaInicio() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FechaInicio is required");
        } else if (reservaDto.getFechaFin() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "FechaFin is required");
        }
        List<HabitacionDto> habitacionesLibres = habitacionService.getHabitacionesLibres(reservaDto.getFechaInicio(), reservaDto.getFechaFin());
        return habitacionesLibres;
    }

    @DeleteMapping(value = "/delete")
    public int deleteHabitacion(@RequestBody HabitacionDto habitacionDto){
        if (habitacionDto.getIdHabitacion() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "IdHabitacion is required");
        }
        if(getHabitacionById(habitacionDto.getIdHabitacion()) == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitacion not found");
        }
        return habitacionService.deleteHabitacion(habitacionDto);
    }
    @GetMapping(value = "/getHabitacionById/{idHabitacion}")
    public HabitacionDto getHabitacionById(@PathVariable int idHabitacion) {
        HabitacionDto habitacionDto = habitacionService.getHabitacionById(idHabitacion);
        if (habitacionDto == null){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Habitacion not found");
        }
        return habitacionService.getHabitacionById(idHabitacion);
    }
}


