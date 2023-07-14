package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.HotelDao;
import com.hotel.continental.model.core.tools.Extras;
import com.hotel.continental.model.core.tools.Messages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;


import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class HotelServiceTest {
    @Mock
    static
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    static
    HotelService hotelService;

    @Mock
    HotelDao hotelDao;

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("hotelInsert")
    void testHotelInsert(String testCaseName, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = hotelService.hotelInsert(attrMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> hotelInsert() {
        return Stream.of(
                //region Test Case 1 - Insert hotel with correct data
                Arguments.of(
                        "Insert hotel with correct data",
                        Map.of(HotelDao.ADDRESS, "address", HotelDao.NAME, "name"),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erInsert = new EntityResultMapImpl();
                                    erInsert.put(HotelDao.HOTEL_ID, List.of(1));

                                    return when(daoHelper.insert(Mockito.any(HotelDao.class), Mockito.anyMap())).thenReturn(erInsert);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Insert hotel with null data
                Arguments.of(
                        "Insert hotel with null data",
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("hotelQuery")
    void testHotelQuery(String testCaseName, Map<String, Object> keyMap, List<String> attrList,EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = hotelService.hotelQuery(keyMap, attrList);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> hotelQuery() {
        return Stream.of(
                //region Test Case 1 - Query hotel with correct data with filter
                Arguments.of(
                        "Query hotel with correct data",
                        Map.of(HotelDao.HOTEL_ID, 1),
                        List.of(HotelDao.HOTEL_ID, HotelDao.ADDRESS, HotelDao.NAME),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(HotelDao.HOTEL_ID, List.of(1));
                                    erQuery.put(HotelDao.ADDRESS, List.of("address"));
                                    erQuery.put(HotelDao.NAME, List.of("name"));
                                    erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);

                                    return when(daoHelper.query(Mockito.any(HotelDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endRegion
                //region Test Case 2 - Query hotel with correct data without filter
                Arguments.of(
                        "Query hotel with correct data",
                        Map.of(),
                        List.of(HotelDao.HOTEL_ID, HotelDao.ADDRESS, HotelDao.NAME),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(HotelDao.HOTEL_ID, List.of(1));
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
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("hotelUpdate")
    void testHotelUpdate(String testCaseName, Map<?, ?> keyMap, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = hotelService.hotelUpdate(attrMap, keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> hotelUpdate() {
        return Stream.of(
                //region Test Case 1 - Update hotel with correct data
                Arguments.of(
                        "Update hotel with correct data",
                        Map.of(HotelDao.HOTEL_ID, 1),
                        Map.of(HotelDao.ADDRESS, "addressUpdate", HotelDao.NAME, "nameUpdate"),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(HotelDao.HOTEL_ID, List.of(1));
                                    erQuery.put(HotelDao.ADDRESS, List.of("address"));
                                    erQuery.put(HotelDao.NAME, List.of("name"));

                                    return when(daoHelper.query(Mockito.any(HotelDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erUpdate = new EntityResultMapImpl();
                                    erUpdate.put(HotelDao.HOTEL_ID, List.of(1));
                                    erUpdate.put(HotelDao.ADDRESS, List.of("addressUpdate"));
                                    erUpdate.put(HotelDao.NAME, List.of("nameUpdate"));

                                    return when(daoHelper.update(Mockito.any(HotelDao.class), Mockito.anyMap(), anyMap())).thenReturn(erUpdate);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Update hotel wrong hotel doesnt exist
                Arguments.of(
                        "Update hotel wrong hotel doesnt exist",
                        Map.of(HotelDao.HOTEL_ID, 1),
                        Map.of(HotelDao.ADDRESS, "addressUpdate", HotelDao.NAME, "nameUpdate"),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.HOTEL_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.setCode(EntityResult.OPERATION_WRONG);

                                    return when(daoHelper.query(Mockito.any(HotelDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Update hotel with null data
                Arguments.of(
                        "Update hotel with null data",
                        Map.of(),
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),
                        List.of()
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("hotelDelete")
    void testHotelDelete(String testCaseName, Map<String, Object> keyMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = hotelService.hotelDelete(keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> hotelDelete() {
        return Stream.of(
                //region Test Case 1 - Delete hotel with correct data
                Arguments.of(
                        "Delete hotel with correct data",
                        Map.of(HotelDao.HOTEL_ID, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, "Hotel dado de baja correctamente con fecha " + new SimpleDateFormat("yyyy-MM-dd").format(new Date())),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(HotelDao.HOTEL_ID, List.of(1));
                                    erQuery.put(HotelDao.ADDRESS, List.of("address"));
                                    erQuery.put(HotelDao.NAME, List.of("name"));

                                    return when(daoHelper.query(Mockito.any(HotelDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erUpdate = new EntityResultMapImpl();
                                    erUpdate.put(HotelDao.HOTEL_ID, List.of(1));
                                    erUpdate.put(HotelDao.ADDRESS, List.of("address"));
                                    erUpdate.put(HotelDao.NAME, List.of("name"));
                                    erUpdate.put(HotelDao.HOTELDOWNDATE, List.of(LocalDateTime.now()));

                                    return when(daoHelper.update(Mockito.any(HotelDao.class), Mockito.anyMap(), anyMap())).thenReturn(erUpdate);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Update hotel wrong hotel doesnt exist
                Arguments.of(
                        "Update hotel wrong hotel doesnt exist",
                        Map.of(HotelDao.HOTEL_ID, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.HOTEL_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.setCode(EntityResult.OPERATION_WRONG);

                                    return when(daoHelper.query(Mockito.any(HotelDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Update hotel with null data
                Arguments.of(
                        "Update hotel with null data",
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),
                        List.of()
                )
                //endregion
        );
    }
}