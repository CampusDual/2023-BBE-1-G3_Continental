package com.hotel.Continental.controller;

import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.api.IHabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

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
}


