package com.hotel.Continental.controller;

import com.hotel.Continental.model.dto.HotelDTO;
import com.hotel.Continental.service.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/hotel")
public class HotelController {
    @Autowired
    private HotelService hotelService;

    @PostMapping("/get")
    public HotelDTO getHotel(@RequestBody HotelDTO hotelDTO) {
        return hotelService.queryHotel(hotelDTO);
    }

    @GetMapping(value = "/getAll")
    public List<HotelDTO> getAllHotels() {
        return hotelService.queryAll();
    }

    @PostMapping(value = "/add")
    public int addHotel(@RequestBody HotelDTO hotelDTO) {
        return hotelService.insertHotel(hotelDTO);
    }
}
