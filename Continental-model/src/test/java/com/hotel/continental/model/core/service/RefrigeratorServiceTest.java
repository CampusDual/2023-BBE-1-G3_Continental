package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.RefrigeratorsDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.service.RefrigeratorsService;
import com.hotel.continental.model.core.tools.Extras;
import com.hotel.continental.model.core.tools.Messages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RefrigeratorServiceTest {
    @Mock
    static
    DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    RefrigeratorsService refrigeratorsService;
    @Mock
    RoomDao roomDao;
    @Mock
    RefrigeratorsDao refrigeratorsDao;

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("refrigeratorInsert")
    void testRefrigeratorInsert(String testCaseName, Map<String, Object> attr, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = refrigeratorsService.refrigeratorsInsert(attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> refrigeratorInsert() {
        return Stream.of(
                //region Test Case 1: Insert Refrigerator with correct data
                Arguments.of(
                        "Insert Refrigerator with correct data",
                        Map.of(RefrigeratorsDao.ROOM_ID, 1, RefrigeratorsDao.CAPACITY, 100),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryRoom = new EntityResultMapImpl();
                                    erQueryRoom.put(RoomDao.ROOM_ID, List.of(1));
                                    return when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(erQueryRoom);
                                },
                                (Supplier) () -> {
                                    EntityResult erInsertFridge = new EntityResultMapImpl();
                                    erInsertFridge.put(RoomDao.ROOM_ID, 1);
                                    return when(daoHelper.insert(any(RefrigeratorsDao.class), anyMap())).thenReturn(erInsertFridge);
                                }
                        )
                ),
                //endregion
                //region Test Case 2: Insert Refrigerator with null data
                Arguments.of(
                        "Insert Refrigerator with null data",
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endregion
                //region Test Case 3: Insert Refrigerator with fail data (not number)
                Arguments.of(
                        "Insert Refrigerator with fail data (not number)",
                        Map.of(RefrigeratorsDao.ROOM_ID, 1, RefrigeratorsDao.CAPACITY, "a"),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.CAPACITY_NOT_NUMBER),
                        List.of()
                ),
                //endregion
                //region Test Case 4: Insert Refrigerator with fail data (not positive)
                Arguments.of(
                        "Insert Refrigerator with fail data (not positive)",
                        Map.of(RefrigeratorsDao.ROOM_ID, 1, RefrigeratorsDao.CAPACITY, -100),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.CAPACITY_NOT_POSITIVE),
                        List.of()
                ),
                //endregion
                //region Test Case 5: Insert Refrigerator with fail data (not exist room)
                Arguments.of(
                        "Insert Refrigerator with fail data (not exist room)",
                        Map.of(RefrigeratorsDao.ROOM_ID, 1, RefrigeratorsDao.CAPACITY, 100),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROOM_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryRoom = new EntityResultMapImpl();
                                    return when(daoHelper.query(any(RoomDao.class), anyMap(), anyList())).thenReturn(erQueryRoom);
                                }
                        )
                )
                //endregion
        );
    }
}
