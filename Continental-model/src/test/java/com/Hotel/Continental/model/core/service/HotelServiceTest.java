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

import javax.management.Query;
import javax.swing.text.Keymap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
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
    public class hotelServiceQuery {
        @Test
        void testQueryHotel() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("nombre", List.of("prueba"));
            er.put("direccion", List.of("direccionPrueba"));

            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put("id", 0);

            List<String> attrList = List.of("id", "nombre", "direccion");

            EntityResult queryResult = hotelService.hotelQuery(keyMap, attrList);

            assertEquals(0, queryResult.getCode(), "El código de retorno de hotelInsert() no es igual a 0");

        }

        @Test
        void testQueryHotelEmpty() {
            EntityResult er = new EntityResultMapImpl();

            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult queryResult = hotelService.hotelQuery(new HashMap<>(), List.of());

            assert queryResult.isEmpty();
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class testUpdateHotel {
        @Test
        void testUpdateHotel() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            Map<String, Object> hotelToInsert = new HashMap<>();

            hotelToInsert.put("id", 1);
            hotelToInsert.put("nombre", "prueba");
            hotelToInsert.put("direccion", "direccionPrueba");

            when(daoHelper.insert(any(HotelDao.class), anyMap())).thenReturn(er);
            Map<String, Object> hotelToUpdate = new HashMap<>();

            hotelToUpdate.put("nombre", "actualizado");
            hotelToUpdate.put("direccion", "direccionPruebaactualizada");

            Map<String, Object> list = new HashMap<>() {{
                put("id", 1);
            }};
            when(daoHelper.update(any(HotelDao.class), anyMap(), anyMap())).thenReturn(er);

            EntityResult result = hotelService.hotelUpdate(list, hotelToUpdate);

            assertEquals(0, result.getCode());

        }
    }
}
