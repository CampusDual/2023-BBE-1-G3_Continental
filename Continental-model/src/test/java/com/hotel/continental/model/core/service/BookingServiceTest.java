package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class BookingServiceTest {
    @Mock
    static DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    BookingService bookingService;

    @Mock
    static RoomService roomService;

    @Mock
    BookingDao bookingDao;


    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("bookingInsertTestData")
    @DisplayName("TestParametrized booking Insert")
    void testBookingInsert(String name, Map<String, Object> attr, EntityResult expectedResult, List<Supplier> mocks) {
        //For each mock, execute the get method, to make sure the mock is called
        mocks.forEach(mockMap -> {
            mockMap.get();
        });
        EntityResult result = bookingService.bookingInsert(attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> bookingInsertTestData() {
        return Stream.of(
                // Test case 1: Successful insert
                Arguments.of(
                        "Successful Insert",//Nombre del test
                        new HashMap<String, Object>() {{
                            put(BookingDao.STARTDATE, "2023-06-09T10:31:10.000+0000");
                            put(BookingDao.ENDDATE, "2023-10-09T10:31:10.000+0000");
                            put(BookingDao.CLIENT, 0);
                        }},//Atributos-Columnas-Keymap
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erRoom = new EntityResultMapImpl();
                                    erRoom.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erRoom.put(RoomDao.IDHOTEL, List.of(0));
                                    erRoom.put(RoomDao.IDHABITACION, List.of(2));
                                    erRoom.put(RoomDao.ROOMNUMBER, List.of(10));
                                    return Mockito.when(roomService.freeRoomsQuery(Mockito.anyMap(), Mockito.anyList())).thenReturn(erRoom);
                                },
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.insert(Mockito.any(BookingDao.class), Mockito.anyMap())).thenReturn(erReserva);
                                }
                        )//Mocks
                ),
                // Test case 2: Insert no free rooms
                Arguments.of(
                        "Insert no free rooms",
                        new HashMap<String, Object>() {{
                            put(BookingDao.STARTDATE, "2023-06-09T10:31:10.000+0000");
                            put(BookingDao.ENDDATE, "2023-10-09T10:31:10.000+0000");
                            put(BookingDao.CLIENT, 0);
                        }},
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.ROOM_NOT_FREE),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erRoom = new EntityResultMapImpl();
                                    erRoom.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(roomService.freeRoomsQuery(Mockito.anyMap(), Mockito.anyList())).thenReturn(erRoom);
                                }
                        )
                ),
                // Test case 3: Insert empty data
                Arguments.of(
                        "Insert empty data",
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_DATA),
                        List.of()
                )

        );
    }



    @ParameterizedTest(name = "{0}")
    @MethodSource("bookingQueryTestData")
    @DisplayName("TestParametrized booking query")
    void testBookingQuery(String name, Map<String, Object> keyMap, List<String> attr, EntityResult expectedResult, List<Supplier> mocks) {
        //For each mock, execute the get method, to make sure the mock is called
        mocks.forEach(mockMap -> {
            mockMap.get();
        });
        EntityResult result = bookingService.bookingQuery(keyMap, attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> bookingQueryTestData() {
        return Stream.of(
                // Test case 1: Successful delete
                Arguments.of(
                        "Test case 1: Successful query",
                        Map.of(BookingDao.BOOKINGID, 0),
                        List.of(BookingDao.BOOKINGID),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReserva.put(BookingDao.BOOKINGID, List.of(0));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva);
                                }
                        )
                ),
                // Test case 2: Booking empty data
                Arguments.of(
                        "Test case 2: Booking empty data",
                        Map.of(),
                        List.of(BookingDao.BOOKINGID),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_NOT_EXIST),
                        List.of()
                ),
                // Test case 3: Booking not found
                Arguments.of(
                        "Test case 3: Booking not found",
                        Map.of(BookingDao.BOOKINGID, 0),
                        List.of(BookingDao.BOOKINGID),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva);
                                }
                        )
                )

        );
    }



    @ParameterizedTest(name = "{0}")
    @MethodSource("bookingUpdateTestData")
    @DisplayName("TestParametrized booking update")
    void testBookingDelete(String name, Map<String, Object> keyMap, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mocks) {
        //For each mock, execute the get method, to make sure the mock is called
        mocks.forEach(mockMap -> {
            mockMap.get();
        });
        EntityResult result = bookingService.bookingUpdate(attrMap, keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> bookingUpdateTestData() {
        return Stream.of(
                // Test case 1: Successful update
                Arguments.of(
                        "Test case 1: Successful update",
                        Map.of(BookingDao.BOOKINGID, 0),
                        Map.of(BookingDao.CLIENT, 0),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReserva.put(BookingDao.BOOKINGID, List.of(0));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva);
                                },
                                (Supplier) () -> {
                                    EntityResult er = new EntityResultMapImpl();
                                    er.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.update(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(er);
                                }
                        )
                ),
                // Test case 2: Booking not found
                Arguments.of(
                        "Test case 2: Booking not found",
                        Map.of(BookingDao.BOOKINGID, 0),
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_WRONG);
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva);
                                }
                        )
                ),
                // Test case 3: Booking empty data
                Arguments.of(
                        "Test case 3: Booking empty data",
                        Map.of(),
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_KEY),
                        List.of()
                )

        );
    }



    @ParameterizedTest(name = "{0}")
    @MethodSource("bookingDeleteTestData")
    @DisplayName("TestParametrized booking delete")
    void testBookingDelete(String name, Map<String, Object> keyMap, EntityResult expectedResult, List<Supplier> mocks) {
        //For each mock, execute the get method, to make sure the mock is called
        mocks.forEach(mockMap -> {
            mockMap.get();
        });
        EntityResult result = bookingService.bookingDelete(keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> bookingDeleteTestData() {
        return Stream.of(
                // Test case 1: Successful delete
                Arguments.of(
                        "Test case 1: Successful delete",
                        Map.of(BookingDao.BOOKINGID, 0),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReserva.put(BookingDao.BOOKINGID, List.of(0));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva);
                                },
                                (Supplier) () -> {
                                    EntityResult er = new EntityResultMapImpl();
                                    er.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.delete(Mockito.any(BookingDao.class), Mockito.anyMap())).thenReturn(er);
                                }
                        )
                ),
                // Test case 2: Booking not found
                Arguments.of(
                        "Test case 2: Booking not found",
                        Map.of(BookingDao.BOOKINGID, 0),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva);
                                }
                        )
                ),
                // Test case 3: Booking empty data
                Arguments.of(
                        "Test case 3: Booking empty data",
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_KEY),
                        List.of()
                )

        );
    }



    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("bookingCheckinTestData")
    @DisplayName("TestParametrized booking check-in")
    void testBookingCheckin(String name, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mocks) {
        //For each mock, execute the get method, to make sure the mock is called
        mocks.forEach(mockMap -> {
            mockMap.get();
        });
        EntityResult result = bookingService.bookingCheckin(attrMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> bookingCheckinTestData() {
        return Stream.of(
                // Test case 1: Successful check-in
                Arguments.of(
                        "Successful check-in",
                        Map.of(BookingDao.BOOKINGID, 1, BookingDao.CLIENT, 2),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ErrorMessages.BOOKING_CHECK_IN_SUCCESS),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReserva.put(BookingDao.BOOKINGID, List.of(0));
                                    EntityResult erReservaCliente = new EntityResultMapImpl();
                                    erReservaCliente.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReservaCliente.put(BookingDao.CLIENT, List.of(0));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva, erReservaCliente);
                                },
                                (Supplier) () -> {
                                    EntityResult er = new EntityResultMapImpl();
                                    er.setCode(0);
                                    er.put(BookingDao.CLIENT, List.of(0));
                                    return Mockito.when(daoHelper.update(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(er);
                                }
                        )
                ),
                // Test case 2: Check-in with invalid booking id
                Arguments.of(
                        "Check-in with invalid booking id",
                        Map.of(BookingDao.BOOKINGID, 1, BookingDao.CLIENT, 2),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva);
                                }
                        )
                ),
                // Test case 3: Check-in with invalid bookingid-clientid combination
                Arguments.of(
                        "Check-in with invalid bookingid-clientid combination",
                        Map.of(BookingDao.BOOKINGID, 1, BookingDao.CLIENT, 2),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_DOESNT_BELONG_CLIENT),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReserva.put(BookingDao.BOOKINGID, List.of(0));
                                    EntityResult erReservaCliente = new EntityResultMapImpl();
                                    erReservaCliente.setCode(EntityResult.OPERATION_WRONG);
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva, erReservaCliente);
                                }
                        )
                ),
                // Test case 4: Already checked-in
                Arguments.of(
                        "Already checked-in",
                        Map.of(BookingDao.BOOKINGID, 1, BookingDao.CLIENT, 2),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_ALREADY_CHECKED_IN),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReserva.put(BookingDao.BOOKINGID, List.of(0));
                                    erReserva.put(BookingDao.CHECKIN_DATETIME, List.of(LocalDateTime.now()));
                                    EntityResult erReservaCliente = new EntityResultMapImpl();
                                    erReservaCliente.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReservaCliente.put(BookingDao.CLIENT, List.of(0));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva, erReservaCliente);
                                }
                        )
                ),
                // Test case 5: Missing booking ID
                Arguments.of(
                        "Missing booking ID",
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_KEY),
                        List.of()
                )

        );
    }

    @Disabled
    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("bookingCheckOutTestData")
    @DisplayName("TestParametrized booking check-out")
    void testBookingCheckout(String name, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mocks) {
        //For each mock, execute the get method, to make sure the mock is called
        mocks.forEach(mockMap -> {
            mockMap.get();
        });
        EntityResult result = bookingService.bookingCheckout(attrMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> bookingCheckOutTestData() {
        return Stream.of(
                // Test case 1: Successful check-in
                Arguments.of(
                        "Successful check-out",
                        Map.of(BookingDao.BOOKINGID, 1, BookingDao.CLIENT, 2),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ErrorMessages.BOOKING_CHECK_OUT_SUCCESS),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReserva.put(BookingDao.BOOKINGID, List.of(0));
                                    erReserva.put(BookingDao.CHECKIN_DATETIME, List.of(LocalDateTime.now()));
                                    EntityResult erReservaCliente = new EntityResultMapImpl();
                                    erReservaCliente.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReservaCliente.put(BookingDao.CLIENT, List.of(0));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva, erReservaCliente);
                                },
                                (Supplier) () -> {
                                    EntityResult er = new EntityResultMapImpl();
                                    er.setCode(0);
                                    er.put(BookingDao.CLIENT, List.of(0));
                                    return Mockito.when(daoHelper.update(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(er);
                                }
                        )
                ),
                // Test case 2: Check-in with invalid booking id
                Arguments.of(
                        "Check-out with invalid booking id",
                        Map.of(BookingDao.BOOKINGID, 1, BookingDao.CLIENT, 2),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva);
                                }
                        )
                ),
                // Test case 3: Check-in with invalid bookingid-clientid combination
                Arguments.of(
                        "Check-out with invalid bookingid-clientid combination",
                        Map.of(BookingDao.BOOKINGID, 1, BookingDao.CLIENT, 2),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_DOESNT_BELONG_CLIENT),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReserva.put(BookingDao.BOOKINGID, List.of(0));
                                    EntityResult erReservaCliente = new EntityResultMapImpl();
                                    erReservaCliente.setCode(EntityResult.OPERATION_WRONG);
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva, erReservaCliente);
                                }
                        )
                ),
                // Test case 4: Already checked-in
                Arguments.of(
                        "Already checked-out",
                        Map.of(BookingDao.BOOKINGID, 1, BookingDao.CLIENT, 2),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_ALREADY_CHECKED_OUT),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erReserva = new EntityResultMapImpl();
                                    erReserva.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReserva.put(BookingDao.BOOKINGID, List.of(0));
                                    erReserva.put(BookingDao.CHECKIN_DATETIME, List.of(LocalDateTime.now()));
                                    erReserva.put(BookingDao.CHECKOUT_DATETIME, List.of(LocalDateTime.now()));
                                    EntityResult erReservaCliente = new EntityResultMapImpl();
                                    erReservaCliente.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erReservaCliente.put(BookingDao.CLIENT, List.of(0));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erReserva, erReservaCliente);
                                }
                        )
                ),
                // Test case 5: Missing booking ID
                Arguments.of(
                        "Missing booking ID",
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_KEY),
                        List.of()
                )

        );
    }

    /**
     * Creates an EntityResult with the given code and message
     *
     * @param code    EntityResult code
     * @param message EntityResult message
     * @return EntityResult
     */
    private static EntityResult createEntityResult(int code, String message) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(code);
        er.setMessage(message);
        return er;
    }
}
