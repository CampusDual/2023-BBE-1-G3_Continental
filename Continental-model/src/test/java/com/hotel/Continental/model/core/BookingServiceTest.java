package com.hotel.Continental.model.core;

import com.hotel.Continental.model.core.dao.BookingDao;
import com.hotel.Continental.model.core.dao.RoomDao;
import com.hotel.Continental.model.core.service.BookingService;
import com.hotel.Continental.model.core.service.RoomService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    BookingDao bookingDao;

    @Mock
    RoomService roomService;

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class bookingServiceInsert {
        @Test
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
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class bookingServiceQuery {
        @Test
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
        void testQueryBookingEmpty(){
            EntityResult er = new EntityResultMapImpl();

            when(daoHelper.query(any(BookingDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = bookingService.bookingQuery(new HashMap<>(), List.of());

            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }

    @Nested
    @TestInstance(Lifecycle.PER_CLASS)
    public class bookingServiceUpdate {
        @Test
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
        void testUpdateBookingEmpty(){
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
        void testUpdateBookingNull(){
            EntityResult result = bookingService.bookingUpdate(null, null);

            assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }
}
