package com.hotel.Continental.api;

import com.hotel.Continental.model.dto.HotelDTO;

import java.util.List;

public interface IHotelService {
    HotelDTO queryHotel(HotelDTO hotelDto);
    List<HotelDTO> queryAll();
    int insertHotel(HotelDTO hotelDto);
    int updateHotel(HotelDTO hotelDto);
    int deleteHotel(HotelDTO hotelDto);

}
