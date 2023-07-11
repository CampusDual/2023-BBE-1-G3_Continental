package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.HotelDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.dao.RoomTypeDao;
import com.hotel.continental.model.core.service.RoomService;
import com.hotel.continental.model.core.tools.Messages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
    @Mock
    static
    DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    RoomService roomService;
    @Mock
    RoomDao roomDao;
    @Mock
    HotelDao hotelDao;
    @Mock
    RoomTypeDao roomTypeDao;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestRoomInsert {
        @Test
        @DisplayName("Test room insert good")
        void testRoomInsertGood() {
            //Primero se hace una query para comprobar que el hotel existe
            EntityResult erQueryHotel = new EntityResultMapImpl();
            erQueryHotel.setCode(EntityResult.OPERATION_SUCCESSFUL);
            erQueryHotel.put(HotelDao.ID, List.of(1));
            //Despues se hace una query para comprobar que la habitacion no existe
            EntityResult erQueryHabitacion = new EntityResultMapImpl();
            erQueryHabitacion.setCode(EntityResult.OPERATION_SUCCESSFUL);
            //Se comprueba que el tipo existe
            EntityResult erQueryTipo = new EntityResultMapImpl();
            erQueryTipo.setCode(EntityResult.OPERATION_SUCCESSFUL);
            erQueryTipo.put(RoomTypeDao.TYPE, List.of(1));
            //Despues se hace el insert
            EntityResult erInsert = new EntityResultMapImpl();
            erInsert.setCode(EntityResult.OPERATION_SUCCESSFUL);
            Map<String, Object> roomToInsert = new HashMap<>();
            roomToInsert.put(RoomDao.ROOMDOWNDATE, null);
            roomToInsert.put(RoomDao.ROOMNUMBER, 1);
            roomToInsert.put(RoomDao.IDHOTEL, 1);
            roomToInsert.put(RoomDao.ROOMTYPEID, 1);
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoomDao.IDHABITACION, 1);

            when(daoHelper.query(any(HotelDao.class),anyMap(), anyList())).thenReturn(erQueryHotel);
            when(daoHelper.query(any(RoomTypeDao.class),anyMap(),anyList())).thenReturn(erQueryTipo);
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(erQueryHabitacion);
            when(daoHelper.insert(any(RoomDao.class), anyMap())).thenReturn(erInsert);

            EntityResult result = roomService.roomInsert(roomToInsert);
            Assertions.assertEquals(0, result.getCode());
        }
    
        @ParameterizedTest
        @ArgumentsSource(testInsertRoomNullAndEmptyData.class)
        @DisplayName("Test room insert no data")
        void testRoomInsertEmptyAndNullData(HashMap<String, Object> roomToInsert) {
            //No hace falta mockear nada porque falla al comprobar los datos
            EntityResult result = roomService.roomInsert(roomToInsert);
            Assertions.assertEquals(1, result.getCode());
        }
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("hotelInsert")
    void testHotelInsert(String testCaseName, Map<String, Object> keyMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = roomService.roomInsert(keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> hotelInsert() {
        return Stream.of(
                //region Test Case 1 - Insert room with correct data with filter
                Arguments.of(
                        "Insert room with correct data",
                        Map.of(HotelDao.ADDRESS, "adress",HotelDao.NAME, "name"),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erInsert = new EntityResultMapImpl();
                                    erInsert.put(HotelDao.ID, List.of(1));
                                    erInsert.put(HotelDao.ADDRESS, List.of("address"));
                                    erInsert.put(HotelDao.NAME, List.of("name"));

                                    return when(daoHelper.insert(Mockito.any(HotelDao.class), Mockito.anyMap())).thenReturn(erInsert);
                                }
                        )
                ),
                //endRegion
                //region Test Case 2 - Insert hotel with correct data without filter
                Arguments.of(
                        "Insert room with correct data",
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(HotelDao.ID, List.of(1));
                                    erQuery.put(HotelDao.ADDRESS, List.of("address"));
                                    erQuery.put(HotelDao.NAME, List.of("name"));
                                    erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);

                                    return when(daoHelper.query(Mockito.any(HotelDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 3 - Query hotel with null data
                Arguments.of(
                        "Query hotel with null data",
                        Map.of(),
                        List.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                )
                //endregion
        );
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestRoomQuery {
        @Test
        @DisplayName("Test room query good")
        void testRoomQueryGood() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("ROOMNUMBER", List.of(1));

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoomDao.IDHABITACION, 1);

            List<Object> attr = new ArrayList<>();
            attr.add(RoomDao.ROOMNUMBER);

            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roomService.roomQuery(keyMap, attr);

            Assertions.assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("Test room query bad")
        void testRoomQueryBad() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoomDao.IDHABITACION, 1);

            List<Object> attr = new ArrayList<>();
            attr.add(RoomDao.ROOMNUMBER);

            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roomService.roomQuery(keyMap, attr);

            Assertions.assertEquals(1, result.getCode());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestRoomUpdate {
        @Test
        @DisplayName("Test room update good")
        void testRoomUpdateGood() {
            EntityResult query = new EntityResultMapImpl();
            query.setCode(0);
            query.put("ROOMNUMBER", List.of(1));

            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("ROOMNUMBER", List.of(1));

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoomDao.IDHABITACION, 1);

            Map<String, Object> attr = new HashMap<>();
            attr.put(RoomDao.ROOMNUMBER, 1);

            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(query);
            when(daoHelper.update(any(RoomDao.class), anyMap(), anyMap())).thenReturn(er);

            EntityResult result = roomService.roomUpdate(attr, keyMap);
            System.out.println(result.getMessage());
            Assertions.assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("Test room update bad")
        void testRoomUpdateBad() {
            Map<String, Object> keyMap = new HashMap<>();
            Map<String, Object> attr = new HashMap<>();
            attr.put(RoomDao.ROOMNUMBER, 1);
            attr.put(RoomDao.IDHOTEL, 1);

            EntityResult result = roomService.roomUpdate(keyMap, attr);
            Assertions.assertEquals(1, result.getCode());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestRoomDelete {
        @Test
        @DisplayName("Test room delete good")
        void testRoomDeleteGood() {
            EntityResult query = new EntityResultMapImpl();
            query.setCode(0);
            query.put("ROOMNUMBER", List.of(1));

            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoomDao.IDHABITACION, 1);

            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(query);
            when(daoHelper.update(any(RoomDao.class), anyMap(), anyMap())).thenReturn(er);

            EntityResult result = roomService.roomDelete(keyMap);
            Assertions.assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("Test wrong room delete")
        void testRoomDeleteWrong() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoomDao.IDHABITACION, null);

            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = roomService.roomDelete(keyMap);

            Assertions.assertEquals(1, result.getCode());
        }
    }

    public static class testInsertRoomNullAndEmptyData implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(new HashMap<String, Object>() {{
                put(RoomDao.ROOMDOWNDATE, null);
                put(RoomDao.ROOMNUMBER, null);
                put(RoomDao.IDHOTEL, null);
            }}, new HashMap<String, Object>()).map(Arguments::of);
        }
    }

    private static EntityResult createEntityResult(int code, String message) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(code);
        er.setMessage(message);
        return er;
    }
}
