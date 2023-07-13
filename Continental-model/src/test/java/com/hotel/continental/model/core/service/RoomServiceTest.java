package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.HotelDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.dao.RoomTypeDao;
import com.hotel.continental.model.core.tools.Extras;
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

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.logging.SimpleFormatter;
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
                //region Test Case 1 - Insert room with correct data
                Arguments.of(
                        "Insert room with correct data",
                        Map.of(RoomDao.ROOMNUMBER, 112, RoomDao.IDHOTEL, 1, RoomDao.ROOMTYPEID, 2),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, "La habitación ha sido dada de alta con fecha " + new SimpleDateFormat("yyyy-MM-dd").format(new Date())),
                        List.of(
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.IDHOTEL, List.of(1));

                                    return when(daoHelper.query(Mockito.any(HotelDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.ROOMTYPEID, List.of(2));

                                    return when(daoHelper.query(Mockito.any(RoomTypeDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erInsert = new EntityResultMapImpl();
                                    erInsert.put(RoomDao.IDHOTEL, List.of(1));
                                    erInsert.put(RoomDao.ROOMNUMBER, List.of(112));
                                    erInsert.put(RoomDao.ROOMTYPEID, List.of(2));

                                    return when(daoHelper.insert(Mockito.any(RoomDao.class), Mockito.anyMap())).thenReturn(erInsert);
                                }
                        )
                ),
                //endRegion
                //region Test Case 2 - Insert room with null data
                Arguments.of(
                        "Insert room with null data",
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endregion
                //region Test Case 3 - Insert room hotel not exist
                Arguments.of(
                        "Insert room hotel not exist",
                        Map.of(RoomDao.ROOMNUMBER, 112, RoomDao.IDHOTEL, 1, RoomDao.ROOMTYPEID, 2),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.HOTEL_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(Mockito.any(HotelDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 4 - Insert type room hotel not exist
                Arguments.of(
                        "Insert room type room not exist",
                        Map.of(RoomDao.ROOMNUMBER, 112, RoomDao.IDHOTEL, 1, RoomDao.ROOMTYPEID, 2),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.TYPE_NOT_EXISTENT),
                        List.of(
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(HotelDao.ID, List.of(1));

                                    return when(daoHelper.query(Mockito.any(HotelDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(Mockito.any(RoomTypeDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 5 - Insert room already exist
                Arguments.of(
                        "Insert room already exist",
                        Map.of(RoomDao.ROOMNUMBER, 112, RoomDao.IDHOTEL, 1, RoomDao.ROOMTYPEID, 2),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROOM_ALREADY_EXIST),
                        List.of(
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(HotelDao.ID, List.of(1));

                                    return when(daoHelper.query(Mockito.any(HotelDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.ROOMTYPEID, List.of(2));

                                    return when(daoHelper.query(Mockito.any(RoomTypeDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.IDROOM, List.of(1));

                                    return when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("hotelQuery")
    void testHotelQuery(String testCaseName, Map<String, Object> keyMap, List<String> attrList, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = roomService.roomQuery(keyMap, attrList);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> hotelQuery() {
        return Stream.of(
                //region Test Case 1 - Query room with correct data with filter
                Arguments.of(
                        "Query room with correct data",
                        Map.of(RoomDao.ROOMNUMBER, 112, RoomDao.IDHOTEL, 1, RoomDao.ROOMTYPEID, 2),
                        List.of(RoomDao.IDROOM),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.IDROOM, List.of(1));
                                    erQuery.put(RoomDao.IDHOTEL, List.of(1));
                                    erQuery.put(RoomDao.ROOMNUMBER, List.of(112));
                                    erQuery.put(RoomDao.ROOMTYPEID, List.of(2));

                                    return when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endRegion
                //region Test Case 2 - Query room with correct data without filter
                Arguments.of(
                        "Query room with correct data",
                        Map.of(),
                        List.of(RoomDao.IDROOM),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.IDROOM, List.of(1));
                                    erQuery.put(RoomDao.IDHOTEL, List.of(1));
                                    erQuery.put(RoomDao.ROOMNUMBER, List.of(112));
                                    erQuery.put(RoomDao.ROOMTYPEID, List.of(2));

                                    return when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endRegion
                //region Test Case 3 - Query room with null data
                Arguments.of(
                        "Insert room with null data",
                        Map.of(),
                        List.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endRegion
                //region Test Case 4 - Query room with room not exist
                Arguments.of(
                        "Insert room with null data",
                        Map.of(),
                        List.of(RoomDao.IDROOM),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROOM_NOT_EXIST),
                        List.of(
                            (Supplier) () -> {
                                EntityResult erQuery = new EntityResultMapImpl();

                                return when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                            }
                        )
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("hotelUpdate")
    void testHotelUpdate(String testCaseName, Map<?, ?> keyMap, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = roomService.roomUpdate(keyMap, attrMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> hotelUpdate() {
        return Stream.of(
                //region Test Case 1 - Update room with correct data
                Arguments.of(
                        "Update room with correct data",
                        Map.of(RoomDao.ROOMNUMBER, 112, RoomDao.ROOMTYPEID, 2),
                        Map.of(RoomDao.IDROOM, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.ROOMTYPEID, List.of(2));

                                    return when(daoHelper.query(Mockito.any(RoomTypeDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.IDROOM, List.of(1));

                                    return when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erUpdate = new EntityResultMapImpl();
                                    erUpdate.put(RoomDao.ROOMNUMBER, List.of(112));
                                    erUpdate.put(RoomDao.ROOMTYPEID, List.of(2));

                                    return when(daoHelper.update(Mockito.any(RoomDao.class), Mockito.anyMap(), anyMap())).thenReturn(erUpdate);
                                }
                        )
                ),
                //endRegion
                //region Test Case 2 - Update room with null key
                Arguments.of(
                        "Update room with null key",
                        Map.of(),
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),
                        List.of()
                ),
                //endRegion
                //region Test Case 3 - Update room with null data
                Arguments.of(
                        "Update room with null data",
                        Map.of(),
                        Map.of(RoomDao.IDROOM, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endregion
                //region Test Case 4 - Update room hotel not exist
                Arguments.of(
                        "Update room hotel not exist",
                        Map.of(RoomDao.ROOMNUMBER, 112, RoomDao.ROOMTYPEID, 2),
                        Map.of(RoomDao.IDROOM, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROOM_NOT_EXIST),
                        List.of(
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.ROOMTYPEID, List.of(2));

                                    return when(daoHelper.query(Mockito.any(RoomTypeDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 5 - Update room hotel not exist
                Arguments.of(
                        "Update room column not editable",
                        Map.of(RoomDao.ROOMNUMBER, 112, RoomDao.IDHOTEL, 1, RoomDao.ROOMTYPEID, 2),
                        Map.of(RoomDao.IDROOM, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.COLUMN_NOT_EDITABLE),
                        List.of()
                ),
                //endregion
                //region Test Case 6 - Update room typeroom not exist
                Arguments.of(
                        "Update room typeroom not exist",
                        Map.of(RoomDao.ROOMNUMBER, 112, RoomDao.ROOMTYPEID, 2),
                        Map.of(RoomDao.IDROOM, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.TYPE_NOT_EXISTENT),
                        List.of(
                            (Supplier) () -> {
                            EntityResult erQuery = new EntityResultMapImpl();

                            return when(daoHelper.query(Mockito.any(RoomTypeDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                        })
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("hotelDelete")
    void testHotelDelete(String testCaseName, Map<?, ?> keyMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = roomService.roomDelete(keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> hotelDelete() {
        return Stream.of(
                //region Test Case 1 - Delete room with correct data
                Arguments.of(
                        "Delete room with correct data",
                        Map.of(RoomDao.IDROOM, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, "La habitación ha sido dada de baja con fecha " + new SimpleDateFormat("yyyy-MM-dd").format(new Date())),
                        List.of(
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.IDROOM, List.of(1));

                                    return when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erUpdate = new EntityResultMapImpl();
                                    erUpdate.put(RoomDao.ROOMNUMBER, List.of(112));
                                    erUpdate.put(RoomDao.ROOMTYPEID, List.of(2));
                                    erUpdate.put(RoomDao.ROOMDOWNDATE, List.of(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));

                                    return when(daoHelper.update(Mockito.any(RoomDao.class), Mockito.anyMap(), anyMap())).thenReturn(erUpdate);
                                }
                        )
                ),
                //endRegion
                //region Test Case 2 - Delete room with null key
                Arguments.of(
                        "Delete room with null key",
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),
                        List.of()
                ),
                //endRegion
                //region Test Case 3 - Delete room doesn´t exist
                Arguments.of(
                        "Delete room doesnt exist",
                        Map.of(RoomDao.IDROOM, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROOM_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endRegion
                //region Test Case 4 - Delete room already inactive
                Arguments.of(
                        "Delete room already inactive",
                        Map.of(RoomDao.IDROOM, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROOM_ALREADY_INACTIVE),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoomDao.IDROOM, List.of(1));
                                    erQuery.put(RoomDao.ROOMDOWNDATE, List.of(new Date()));

                                    return when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                )
                //endregion
        );
    }
}
