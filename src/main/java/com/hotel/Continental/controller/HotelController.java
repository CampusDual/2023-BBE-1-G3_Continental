package com.hotel.Continental.controller;

import com.hotel.Continental.api.IHotelService;
import com.hotel.Continental.model.dto.HotelDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/hotel")
public class HotelController {
    @Autowired
    private IHotelService ihotelService;

    @PostMapping("/get")
    public HotelDTO getHotel(@RequestBody HotelDTO hotelDTO) {
        return ihotelService.queryHotel(hotelDTO);
    }

    @GetMapping(value = "/getAll")
    public List<HotelDTO> getAllHoteles() {
        return ihotelService.queryAll();
    }

    @PostMapping(value = "/add")
    public int addHotel(@RequestBody HotelDTO hotelDTO) {
        if (hotelDTO.getNombre() == null || hotelDTO.getNombre().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Name is required");
        }else if (hotelDTO.getDireccion() == null || hotelDTO.getDireccion().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Address is required");
        }
        return ihotelService.insertHotel(hotelDTO);
    }
}
