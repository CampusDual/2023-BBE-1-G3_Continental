package com.hotel.Continental.model.core.service;

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
import java.sql.Timestamp;
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


            hotelToInsert.put(HotelDao.NAME, "prueba");
            hotelToInsert.put(HotelDao.ADDRESS, "direccionPrueba");


            when(daoHelper.insert(any(HotelDao.class), anyMap())).thenReturn(er);


            EntityResult result = hotelService.hotelInsert(hotelToInsert);


            assertEquals(0, result.getCode());
        }


        @Test
        void testInsertHotelEmpty() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            Map<String, Object> hotelToInsert = new HashMap<>();


            when(daoHelper.insert(any(HotelDao.class), anyMap())).thenReturn(er);


            EntityResult result = hotelService.hotelInsert(hotelToInsert);


            assertEquals(0, result.getCode());
        }


        @Test
        void testInsertHotelNull() {
            EntityResult er = null;
            Map<String, Object> hotelToInsert = new HashMap<>();


            when(daoHelper.insert(any(HotelDao.class), anyMap())).thenReturn(er);


            EntityResult result = hotelService.hotelInsert(hotelToInsert);


            assertNull(result);
        }
    }


    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class hotelServiceQuery {
        @Test
        void testQueryHotel() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(HotelDao.NAME, List.of("prueba"));
            er.put(HotelDao.ADDRESS, List.of("direccionPrueba"));


            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);


            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, 0);


            List<String> attrList = List.of(HotelDao.ID, HotelDao.NAME, HotelDao.ADDRESS);


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
            er.put(HotelDao.NAME, List.of("pruebaActualizada"));
            er.put(HotelDao.ADDRESS, List.of("direccionActualizada"));


            EntityResult erQuery = new EntityResultMapImpl();
            erQuery.setCode(0);
            erQuery.put(HotelDao.NAME, List.of("prueba"));
            erQuery.put(HotelDao.ADDRESS, List.of("direccionPrueba"));


            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);
            when(daoHelper.update(any(HotelDao.class), anyMap(), anyMap())).thenReturn(erQuery);


            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, List.of(0));


            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put(HotelDao.NAME, List.of("pruebaActualizada"));
            attrMap.put(HotelDao.ADDRESS, List.of("direccionActualizada"));


            EntityResult queryResult = hotelService.hotelUpdate(attrMap, keyMap);


            assertEquals(0, queryResult.getCode(), "El código de retorno de hotelInsert() no es igual a 0");
        }


        @Test
        void testUpdateHotelDoesntExist() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.put(HotelDao.NAME, List.of("pruebaActualizada"));
            er.put(HotelDao.ADDRESS, List.of("direccionActualizada"));


            EntityResult erQuery = new EntityResultMapImpl();
            erQuery.setCode(0);
            erQuery.put(HotelDao.NAME, List.of("prueba"));
            erQuery.put(HotelDao.ADDRESS, List.of("direccionPrueba"));


            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);


            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, List.of(1));


            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put(HotelDao.NAME, List.of("pruebaActualizada"));
            attrMap.put(HotelDao.ADDRESS, List.of("direccionActualizada"));


            EntityResult queryResult = hotelService.hotelUpdate(attrMap, keyMap);


            assertEquals(EntityResult.OPERATION_WRONG, queryResult.getCode());
        }


        @Test
        void testUpdateHotelNull() {
            EntityResult er = null;
            EntityResult erQuery = new EntityResultMapImpl();
            erQuery.setCode(0);
            erQuery.put(HotelDao.NAME, List.of("prueba"));
            erQuery.put(HotelDao.ADDRESS, List.of("direccionPrueba"));


            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);


            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, List.of(1));


            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put(HotelDao.NAME, List.of("pruebaActualizada"));
            attrMap.put(HotelDao.ADDRESS, List.of("direccionActualizada"));


            EntityResult queryResult = hotelService.hotelUpdate(attrMap, keyMap);


            assertEquals(EntityResult.OPERATION_WRONG, queryResult.getCode());
        }
    }


    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class testDeleteHotel {
        @Test
        void testDeleteHotel() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(HotelDao.NAME, List.of("pruebaDelete"));
            er.put(HotelDao.ADDRESS, List.of("direccionDelete"));


            EntityResult erUpdate = new EntityResultMapImpl();
            erUpdate.setCode(0);
            erUpdate.put(HotelDao.NAME, List.of("pruebaDelete"));
            erUpdate.put(HotelDao.ADDRESS, List.of("direccionDelete"));
            erUpdate.put(HotelDao.HOTELDOWNDATE, new Timestamp(System.currentTimeMillis()));


            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);
            when(daoHelper.update(any(HotelDao.class), anyMap(), anyMap())).thenReturn(erUpdate);


            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, List.of(0));


            EntityResult queryResult = hotelService.hotelDelete(keyMap);


            assertEquals(0, queryResult.getCode());
        }


        @Test
        void testDeleteHotelDoesntExist() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.put(HotelDao.NAME, List.of("pruebaActualizada"));
            er.put(HotelDao.ADDRESS, List.of("direccionActualizada"));


            EntityResult erDelete = new EntityResultMapImpl();
            erDelete.setCode(0);
            erDelete.put(HotelDao.NAME, List.of("prueba"));
            erDelete.put(HotelDao.ADDRESS, List.of("direccionPrueba"));
            erDelete.put(HotelDao.HOTELDOWNDATE, new Timestamp(System.currentTimeMillis()));


            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);


            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, List.of(1));


            EntityResult queryResult = hotelService.hotelDelete(keyMap);


            assertEquals(EntityResult.OPERATION_WRONG, queryResult.getCode());
        }


        @Test
        void testDeleteHotelNull() {
            EntityResult er = null;
            EntityResult erDelete = new EntityResultMapImpl();
            erDelete.setCode(0);
            erDelete.put(HotelDao.NAME, List.of("prueba"));
            erDelete.put(HotelDao.ADDRESS, List.of("direccionPrueba"));
            erDelete.put(HotelDao.HOTELDOWNDATE, new Timestamp(System.currentTimeMillis()));

            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, List.of(1));


            EntityResult queryResult = hotelService.hotelDelete(keyMap);

            assertEquals(EntityResult.OPERATION_WRONG, queryResult.getCode());
        }
    }
}