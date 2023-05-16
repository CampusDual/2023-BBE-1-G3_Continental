package com.hotel.Continental.service;

import com.hotel.Continental.api.IHotelService;
import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dao.HotelDao;
import com.hotel.Continental.model.dto.HotelDTO;
import com.hotel.Continental.model.dto.dtoMapper.HotelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("hotelService")
@Lazy
public class HotelService implements IHotelService {
    @Autowired
    private HotelDao hotelDao;

    @Override
    public HotelDTO queryHotel(HotelDTO hotelDto) {
        Hotel hotel = HotelMapper.INSTANCE.toEntity(hotelDto);
        return HotelMapper.INSTANCE.toDTO(hotelDao.getReferenceById(hotel.getId()));
    }

    @Override
    public List<HotelDTO> queryAll() {
        return null;
    }

    @Override
    public int insertHotel(HotelDTO hotelDto) {
        Hotel hotel=HotelMapper.INSTANCE.toEntity(hotelDto);
        hotelDao.saveAndFlush(hotel);
        return hotel.getId();
    }

    @Override
    public int updateHotel(HotelDTO hotelDto) {
        return 0;
    }

    @Override
    public int deleteHotel(HotelDTO hotelDto) {
        return 0;
    }
}
