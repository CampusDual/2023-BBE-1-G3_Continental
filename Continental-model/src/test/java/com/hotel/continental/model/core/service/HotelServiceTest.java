package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.ClientDao;
import com.hotel.continental.model.core.dao.HotelDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


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
    class hotelServiceInsert {
        @Test
        @DisplayName("Test good insert hotel")
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

        @ParameterizedTest
        @ArgumentsSource(testInsertHotelNullAndEmptyData.class)
        @DisplayName("Test insert hotel with null and empty data")
        void testInsertHotelNullAndEmptyData(HashMap<String, Object> hotelToInsert) {
            //No hace falta mockear nada porque lanza error antes
            EntityResult result = hotelService.hotelInsert(hotelToInsert);
            assertEquals(1, result.getCode());
        }
    }


    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class hotelServiceQuery {
        @Test
        @DisplayName("Test good query hotel")
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
        @DisplayName("Test insert empty data")
        void testQueryHotelEmpty() {
            EntityResult er = new EntityResultMapImpl();

            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult queryResult = hotelService.hotelQuery(new HashMap<>(), List.of());

            assertTrue(queryResult.isEmpty());
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class hotelUpdateHotel {
        @Test
        @DisplayName("Test good update hotel")
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
        @DisplayName("Test bad update hotel doesnt exist")
        void testUpdateHotelDoesntExist() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.put(HotelDao.NAME, List.of("pruebaActualizada"));
            er.put(HotelDao.ADDRESS, List.of("direccionActualizada"));

            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, List.of(1));

            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put(HotelDao.NAME, List.of("pruebaActualizada"));
            attrMap.put(HotelDao.ADDRESS, List.of("direccionActualizada"));

            EntityResult queryResult = hotelService.hotelUpdate(attrMap, keyMap);

            assertEquals(EntityResult.OPERATION_WRONG, queryResult.getCode());
        }


        @ParameterizedTest
        @NullSource
        @DisplayName("Test update hotel with null data")
        void testUpdateHotelNull(String nullParameter) {
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, nullParameter);

            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put(HotelDao.NAME, nullParameter);
            attrMap.put(HotelDao.ADDRESS, nullParameter);

            EntityResult queryResult = hotelService.hotelUpdate(attrMap, keyMap);

            assertEquals(EntityResult.OPERATION_WRONG, queryResult.getCode());
        }
    }


    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class testDeleteHotel {
        @Test
        @DisplayName("Test good delete hotel")
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
        @DisplayName("Test delete hotel doesnt exist")
        void testDeleteHotelDoesntExist() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.put(HotelDao.NAME, List.of("pruebaActualizada"));
            er.put(HotelDao.ADDRESS, List.of("direccionActualizada"));

            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, List.of(1));

            EntityResult queryResult = hotelService.hotelDelete(keyMap);

            assertEquals(EntityResult.OPERATION_WRONG, queryResult.getCode());
        }


        @Test
        @DisplayName("Test delete hotel with null data")
        void testDeleteHotelNull() {
            EntityResult er = null;

            when(daoHelper.query(any(HotelDao.class), anyMap(), anyList())).thenReturn(er);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(HotelDao.ID, null);

            EntityResult queryResult = hotelService.hotelDelete(keyMap);

            assertEquals(EntityResult.OPERATION_WRONG, queryResult.getCode());
        }
    }

    public static class testInsertHotelNullAndEmptyData implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(new HashMap<String, Object>() {{
                put(HotelDao.NAME, "");
                put(HotelDao.ADDRESS, "");
            }}, new HashMap<String, Object>(){{
                put(HotelDao.NAME, null);
                put(HotelDao.ADDRESS, null);
            }}).map(Arguments::of);
        }
    }
}