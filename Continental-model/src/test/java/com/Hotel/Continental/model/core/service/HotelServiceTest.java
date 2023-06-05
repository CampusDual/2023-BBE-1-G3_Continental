package com.Hotel.Continental.model.core.service;

import com.hotel.Continental.model.core.dao.HotelDao;
import com.hotel.Continental.model.core.service.HotelService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    HotelService hotelService;

    @Mock
    HotelDao hotelDao;

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class hotelServiceInsert {
        @Test
        void testInsertHotel() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            Map<String, Object> hotelToInsert = new HashMap<>();

            hotelToInsert.put("nombre", "prueba");
            hotelToInsert.put("direccion", "direccionPrueba");

            when(daoHelper.insert(any(HotelDao.class), anyMap())).thenReturn(er);

            EntityResult result = hotelService.hotelInsert(hotelToInsert);

            assertEquals(0, result.getCode());
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class hotelServiceGet {
        @Test
        void testInsertHotel() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            Map<String, Object> hotelToInsert = new HashMap<>();

            hotelToInsert.put("nombre", "prueba");
            hotelToInsert.put("direccion", "direccionPrueba");

            when(daoHelper.insert(any(HotelDao.class), anyMap())).thenReturn(er);

            EntityResult result = hotelService.hotelInsert(hotelToInsert);

            assertEquals(0, result.getCode());
        }
    }
}
