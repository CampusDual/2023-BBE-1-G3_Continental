package com.hotel.Continental.controller;

import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.api.IHabitacionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/habitacion")
public class HabitacionController {

    @Autowired
    private IHabitacionService habitacionService;

    @PostMapping(value = "/add")
    public int addHotel(@RequestBody HabitacionDto habitacionDto) {
        return habitacionService.insertHabitacion(habitacionDto);
    }
}


