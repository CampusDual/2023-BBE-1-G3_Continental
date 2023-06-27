package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    BookingService bookingService;

    @Mock
    RoomService roomService;

    @Mock
    BookingDao bookingDao;

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class bookingServiceInsert {
        @Test
        @DisplayName("Test booking insert")
        void testInsertBooking() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(BookingDao.CLIENT, List.of(0));
            er.put(BookingDao.STARTDATE, List.of("2023-06-09T10:31:10.000+0000"));
            er.put(BookingDao.ENDDATE, List.of("2023-10-09T10:31:10.000+0000"));

            EntityResult erRoom = new EntityResultMapImpl();
            erRoom.setCode(0);
            erRoom.put(RoomDao.IDHOTEL, List.of(0));
            erRoom.put(RoomDao.IDHABITACION, List.of(2));
            erRoom.put(RoomDao.ROOMNUMBER, List.of(10));

            when(roomService.freeRoomsQuery(anyMap(), anyList())).thenReturn(erRoom);
            when(daoHelper.insert(any(BookingDao.class), anyMap())).thenReturn(er);

            Map<String, Object> hotelToInsert = new HashMap<>();

            hotelToInsert.put(BookingDao.CLIENT, 0);
            hotelToInsert.put(BookingDao.STARTDATE, "2023-06-09T10:31:10.000+0000");
            hotelToInsert.put(BookingDao.ENDDATE, "2023-10-09T10:31:10.000+0000");

            EntityResult result = bookingService.bookingInsert(hotelToInsert);

            assertEquals(0, result.getCode());
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Test booking insert with null data")
        void testInsertBookingNullData(String nullParameter) {
            Map<String,Object> bookingToUpdate = new HashMap<>();
            bookingToUpdate.put(BookingDao.BOOKINGID, nullParameter);
            bookingToUpdate.put(BookingDao.CLIENT, nullParameter);
            bookingToUpdate.put(BookingDao.ROOMID, nullParameter);

            EntityResult result = bookingService.bookingInsert(bookingToUpdate);
            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class bookingServiceQuery {
        @Test
        @DisplayName("Test booking query")
        void testQueryBooking() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(BookingDao.CLIENT, List.of(0));
            er.put(BookingDao.STARTDATE, List.of("2023-06-09T10:31:10.000+0000"));
            er.put(BookingDao.ENDDATE, List.of("2023-10-09T10:31:10.000+0000"));

            when(daoHelper.query(any(BookingDao.class), anyMap(), anyList())).thenReturn(er);

            Map<String, Object> hotelToQuery = new HashMap<>();
            hotelToQuery.put(BookingDao.CLIENT, 0);

            List<String> list = List.of(BookingDao.CLIENT, BookingDao.STARTDATE, BookingDao.ENDDATE);

            EntityResult result = bookingService.bookingQuery(hotelToQuery, list);

            assertEquals(0, result.getCode());
        }

        @ParameterizedTest
        @NullSource
        @DisplayName("Test booking query with null data")
        void testQueryBookingNullData(String nullParameter) {

            Map<String,Object> bookingToUpdate = new HashMap<>();
            bookingToUpdate.put(BookingDao.BOOKINGID, nullParameter);
            bookingToUpdate.put(BookingDao.CLIENT, nullParameter);
            bookingToUpdate.put(BookingDao.ROOMID, nullParameter);

            List<String> list = List.of(BookingDao.CLIENT, BookingDao.STARTDATE, BookingDao.ENDDATE);

            EntityResult result = bookingService.bookingQuery(bookingToUpdate, list);
            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class bookingServiceUpdate {
        @Test
        @DisplayName("Test booking update")
        void testUpdateBooking() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(BookingDao.CLIENT, List.of(0));
            er.put(BookingDao.ROOMID, List.of(1));
            er.put(BookingDao.STARTDATE, List.of("2023-06-09T10:31:10.000+0000"));
            er.put(BookingDao.ENDDATE, List.of("2023-10-09T10:31:10.000+0000"));

            EntityResult erFreeRooms= new EntityResultMapImpl();
            erFreeRooms.setCode(0);
            erFreeRooms.put(RoomDao.IDHOTEL, List.of(0));
            when(roomService.freeRoomsQuery(anyMap(),anyList())).thenReturn(erFreeRooms);
            when(daoHelper.query(any(BookingDao.class), anyMap(), anyList())).thenReturn(er);
            when(daoHelper.update(any(BookingDao.class), anyMap(), anyMap())).thenReturn(er);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(BookingDao.BOOKINGID, 0);

            Map<String, Object> bookingToUpdate = new HashMap<>();
            bookingToUpdate.put(BookingDao.CLIENT, 2);
            bookingToUpdate.put(BookingDao.ROOMID, 2);
            bookingToUpdate.put(BookingDao.STARTDATE, "2010-06-09");
            bookingToUpdate.put(BookingDao.ENDDATE, "2023-10-09");

            EntityResult result = bookingService.bookingUpdate(bookingToUpdate, keyMap);

            assertEquals(0, result.getCode());
        }

        @ParameterizedTest
        @ArgumentsSource(testUpdateBookingNullAndEmptyData.class)
        @DisplayName("Test booking update with null and empty data")
        void testUpdateBookingNullAndEmptyData(HashMap<String, Object> bookingToUpdate, HashMap<String, Object> keyMap, String errorMessage) {
            EntityResult result = bookingService.bookingUpdate(bookingToUpdate, keyMap);
            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            assertEquals(errorMessage, result.getMessage());
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    class testBookingServiceDelete {
        @Test
        @DisplayName("Test booking delete")
        void testDeleteBooking() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(BookingDao.CLIENT, List.of(0));
            er.put(BookingDao.ROOMID, List.of(1));
            er.put(BookingDao.STARTDATE, List.of("2023-06-09T10:31:10.000+0000"));
            er.put(BookingDao.ENDDATE, List.of("2023-10-09T10:31:10.000+0000"));

            EntityResult erDelete = new EntityResultMapImpl();
            erDelete.setCode(0);

            when(daoHelper.query(any(BookingDao.class), anyMap(), anyList())).thenReturn(er);
            when(daoHelper.delete(any(BookingDao.class), anyMap())).thenReturn(erDelete);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(BookingDao.BOOKINGID, 0);

            EntityResult result = bookingService.bookingDelete(keyMap);

            assertEquals(0, result.getCode());
        }

        @ParameterizedTest
        @ArgumentsSource(testDeleteBookingNullAndEmptyData.class)
        @DisplayName("Test booking delete with null data")
        void testDeleteBookingNull(HashMap<String, Object> bookingToDelete, String errorMessage) {
            EntityResult result = bookingService.bookingDelete(bookingToDelete);
            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            assertEquals(errorMessage, result.getMessage());
        }
    }

    public static class testUpdateBookingNullAndEmptyData implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(new HashMap<String, Object>() {{
                        put(BookingDao.BOOKINGID, null);
                        put(BookingDao.CLIENT, null);
                        put(BookingDao.ROOMID, null);
                    }}, new HashMap<String, Object>(){{
                        put(BookingDao.BOOKINGID, 0);
                    }}, ErrorMessages.BOOKING_NOT_EXIST),
                    Arguments.of(new HashMap<String, Object>(),
                            new HashMap<String, Object>(),
                            ErrorMessages.NECESSARY_KEY));
        }
    }

    public static class testDeleteBookingNullAndEmptyData implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(new HashMap<String, Object>() {{
                        put(BookingDao.BOOKINGID, 1);
                        put(BookingDao.CLIENT, null);
                        put(BookingDao.ROOMID, null);
                    }}, ErrorMessages.BOOKING_NOT_EXIST),
                    Arguments.of(new HashMap<String, Object>(),
                            ErrorMessages.NECESSARY_KEY),
                    Arguments.of(new HashMap<String, Object>(){{
                        put(BookingDao.BOOKINGID, null);
                        put(BookingDao.CLIENT, null);
                        put(BookingDao.ROOMID, null);
                    }}, ErrorMessages.NECESSARY_KEY));
        }
    }
}