package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.RoomTypeDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoomTypeServiceTest {
    @Mock
    static DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    RoomTypeService roomTypeService;
    @Mock
    RoomTypeDao roomTypeDao;

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("roomTypeUpdate")
    void testRoomTypeUpdate(String testCaseName, Map<String, Object> attr, Map<String, Object> keymap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = roomTypeService.roomtypeUpdate(attr, keymap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> roomTypeUpdate() {
        return Stream.of(
                //region Test Case 1 - Update roomtype with correct data
                Arguments.of(
                        "Update roomtype with correct data",
                        Map.of(RoomTypeDao.TYPE, "Suite"),
                        Map.of(RoomTypeDao.TYPEID, 1),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erQuery.put(RoomTypeDao.TYPEID, List.of(1));
                                    return when(daoHelper.query(Mockito.any(RoomTypeDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erUpdate = new EntityResultMapImpl();
                                    erUpdate.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return when(daoHelper.update(Mockito.any(RoomTypeDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(erUpdate);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Update roomtype with null data
                Arguments.of(
                        "Update roomtype with null data",
                        Map.of(),
                        Map.of(RoomTypeDao.TYPEID, 1),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_DATA),
                        List.of()
                ),
                //endregion
                //region Test Case 3 - Update roomtype with null key
                Arguments.of(
                        "Update roomtype with null key",
                        Map.of(RoomTypeDao.TYPE, "Suite"),
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_KEY),
                        List.of()
                ),
                //endregion
                //region Test Case 4 - Update roomtype with no existing key
                Arguments.of(
                        "Update roomtype with no existing key",
                        Map.of(RoomTypeDao.TYPE, "Suite"),
                        Map.of(RoomTypeDao.TYPEID, 1),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.ROOMTYPE_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return when(daoHelper.query(Mockito.any(RoomTypeDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erQuery);
                                }
                        )
                )
                //endregion
        );
    }
    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("roomTypeInsert")
    void testRoomTypeInsert(String testCaseName, Map<String, Object> attrList, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = roomTypeService.roomtypeInsert(attrList);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> roomTypeInsert() {
        return Stream.of(
                //region Test Case 1 - Update roomtype with correct data
                Arguments.of(
                        "Insert roomtype with correct data",
                        Map.of(RoomTypeDao.TYPE, "Suite", RoomTypeDao.PRICE, 20.2),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erInsert = new EntityResultMapImpl();
                                    erInsert.put(RoomTypeDao.TYPEID, 1);
                                    erInsert.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return when(daoHelper.insert(Mockito.any(RoomTypeDao.class), Mockito.anyMap())).thenReturn(erInsert);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Insert roomtype with null data
                Arguments.of(
                        "Insert roomtype with null data",
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_DATA),
                        List.of()
                )
                //endregion
        );
    }

    private static EntityResult createEntityResult(int code, String message) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(code);
        er.setMessage(message);
        return er;
    }
}
