package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import java.util.HashMap;
import java.util.List;
import java.util.Map;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    BookingService bookingService;

    @Mock
    BookingDao bookingDao;

    @Mock
    RoomService roomService;

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class bookingServiceInsert {
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

        @Test
        @DisplayName("Test booking insert with null data")
        void testInsertBookingNullData() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String,Object> bookingToUpdate = new HashMap<>();
            bookingToUpdate.put(BookingDao.BOOKINGID, null);
            bookingToUpdate.put(BookingDao.CLIENT, null);
            bookingToUpdate.put(BookingDao.ROOMID, null);

            EntityResult result = bookingService.bookingInsert(bookingToUpdate);
            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class bookingServiceQuery {
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

        @Test
        @DisplayName("Test booking query with null data")
        void testQueryBookingNullData() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String,Object> bookingToUpdate = new HashMap<>();
            bookingToUpdate.put(BookingDao.BOOKINGID, null);
            bookingToUpdate.put(BookingDao.CLIENT, null);
            bookingToUpdate.put(BookingDao.ROOMID, null);

            List<String> list = List.of(BookingDao.CLIENT, BookingDao.STARTDATE, BookingDao.ENDDATE);

            EntityResult result = bookingService.bookingQuery(bookingToUpdate, list);
            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class bookingServiceUpdate {
        @Test
        @DisplayName("Test booking update")
        void testUpdateBooking() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(BookingDao.CLIENT, List.of(0));
            er.put(BookingDao.ROOMID, List.of(1));
            er.put(BookingDao.STARTDATE, List.of("2023-06-09T10:31:10.000+0000"));
            er.put(BookingDao.ENDDATE, List.of("2023-10-09T10:31:10.000+0000"));


            EntityResult erUpdate = new EntityResultMapImpl();
            erUpdate.setCode(0);
            erUpdate.put(BookingDao.CLIENT, List.of(2));
            erUpdate.put(BookingDao.ROOMID, List.of(2));
            erUpdate.put(BookingDao.STARTDATE, List.of("2010-06-09"));
            erUpdate.put(BookingDao.ENDDATE, List.of("2010-10-09"));


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

        @Test
        @DisplayName("Test booking update with empty data")
        void testUpdateBookingEmpty() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(BookingDao.CLIENT, List.of(0));
            er.put(BookingDao.ROOMID, List.of(1));
            er.put(BookingDao.STARTDATE, List.of("2023-06-09T10:31:10.000+0000"));
            er.put(BookingDao.ENDDATE, List.of("2023-10-09T10:31:10.000+0000"));

            when(daoHelper.query(any(BookingDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = bookingService.bookingUpdate(new HashMap<>(), new HashMap<>());

            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }

        @Test
        @DisplayName("Test booking update with null data")
        void testUpdateBookingNullData() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String,Object> bookingToUpdate = new HashMap<>();
            bookingToUpdate.put(BookingDao.BOOKINGID, null);
            bookingToUpdate.put(BookingDao.CLIENT, null);
            bookingToUpdate.put(BookingDao.ROOMID, null);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(BookingDao.BOOKINGID, 0);

            EntityResult result = bookingService.bookingUpdate(bookingToUpdate, keyMap);
            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class testBookingServiceDelete {
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

        @Test
        @DisplayName("Test booking delete with empty data")
        void testDeleteBookingEmpty() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.put(BookingDao.CLIENT, List.of(0));
            er.put(BookingDao.ROOMID, List.of(1));
            er.put(BookingDao.STARTDATE, List.of("2023-06-09T10:31:10.000+0000"));
            er.put(BookingDao.ENDDATE, List.of("2023-10-09T10:31:10.000+0000"));

            EntityResult erDelete = new EntityResultMapImpl();
            erDelete.setCode(0);
            erDelete.put(BookingDao.CLIENT, List.of(0));
            erDelete.put(BookingDao.ROOMID, List.of(1));
            erDelete.put(BookingDao.STARTDATE, List.of("2023-06-09T10:31:10.000+0000"));
            erDelete.put(BookingDao.ENDDATE, List.of("2023-10-09T10:31:10.000+0000"));

            when(daoHelper.query(any(BookingDao.class), anyMap(), anyList())).thenReturn(er);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(BookingDao.BOOKINGID, 0);

            EntityResult result = bookingService.bookingDelete(keyMap);

            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }

        @Test
        @DisplayName("Test booking delete with null data")
        void testDeleteBookingNull() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String,Object> bookingToDelete = new HashMap<>();
            bookingToDelete.put(BookingDao.BOOKINGID, null);
            bookingToDelete.put(BookingDao.CLIENT, null);
            bookingToDelete.put(BookingDao.ROOMID, null);

            EntityResult result = bookingService.bookingDelete(bookingToDelete);
            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }
}