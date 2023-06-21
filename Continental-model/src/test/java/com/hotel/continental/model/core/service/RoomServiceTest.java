package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.service.RoomService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@Disabled
@ExtendWith(MockitoExtension.class)
public class RoomServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    RoomService roomService;
    @Mock
    RoomDao roomDao;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestRoomInsert {
        @Test
        @DisplayName("Test room insert good")
        void testRoomInsertGood() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String, Object> roomToInsert = new HashMap<>();
            roomToInsert.put(RoomDao.ROOMDOWNDATE, null);
            roomToInsert.put(RoomDao.ROOMNUMBER, 1);
            roomToInsert.put(RoomDao.IDHOTEL, 1);
            when(daoHelper.insert(any(RoomDao.class), anyMap())).thenReturn(er);
            EntityResult result = roomService.roomInsert(roomToInsert);
            Assertions.assertEquals(0, result.getCode());
        }

        @DisplayName("Test room insert bad")
        void testRoomInsertBad(String nullParameter) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            Map<String, Object> roomToInsert = new HashMap<>();
            roomToInsert.put(RoomDao.ROOMDOWNDATE, nullParameter);
            roomToInsert.put(RoomDao.ROOMNUMBER, nullParameter);
            roomToInsert.put(RoomDao.IDHOTEL, nullParameter);
            when(daoHelper.insert(any(RoomDao.class), anyMap())).thenReturn(er);
            EntityResult result = roomService.roomInsert(roomToInsert);
            Assertions.assertEquals(1, result.getCode());
        }
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
            attr.put(RoomDao.IDHOTEL, 1);
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(query);
            when(daoHelper.update(any(RoomDao.class), anyMap(), anyMap())).thenReturn(er);
            EntityResult result = roomService.roomUpdate(keyMap, attr);
            Assertions.assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("Test room update bad")
        void testRoomUpdateBad() {
            EntityResult query = new EntityResultMapImpl();
            query.setCode(0);
            query.put("ROOMNUMBER", List.of(1));
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            Map<String, Object> keyMap = new HashMap<>();
            Map<String, Object> attr = new HashMap<>();
            attr.put(RoomDao.ROOMNUMBER, 1);
            attr.put(RoomDao.IDHOTEL, 1);
            when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(query);
            when(daoHelper.update(any(RoomDao.class), anyMap(), anyMap())).thenReturn(er);
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
}
