package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.ParkingDao;
import com.hotel.continental.model.core.dao.ParkingHistoryDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.tools.Extras;
import com.hotel.continental.model.core.tools.Messages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyMap;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {
    @InjectMocks
    private ParkingService parkingService;
    @Mock
    private static ParkingHistoryService parkingHistoryService;
    @Mock
    private static ParkingDao parkingDao;
    @Mock
    private ParkingHistoryDao parkingHistoryDao;
    @Mock
    private BookingDao bookingDao;
    @Mock
    private static DefaultOntimizeDaoHelper daoHelper;
    @Mock
    private RoomDao roomDao;
    @Mock
    private static ExtraExpensesService extraExpensesService;

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("parkingEnterTestData")
    @DisplayName("TestParametrized parking Enter")
    void testParkingEnter(String name, Map<String, Object> attr, EntityResult expectedResult, List<Supplier> mocks) {
        //For each mock, execute the get method, to make sure the mock is called
        mocks.forEach(mockMap -> {
            mockMap.get();
        });
        EntityResult result = parkingService.parkingEnter(attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> parkingEnterTestData() {
        return Stream.of(
                //region Test case 1: Successful enter
                Arguments.of(
                        "Successful enter",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_SUCCESSFUL,
                                ""
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(5));
                                    erParking.put(ParkingDao.ID_HOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                },
                                // Mock bookingDao
                                (Supplier) () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.STARTDATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ENDDATE, List.of(new Date(LocalDate.now().plusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.CHECKIN_DATETIME, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ROOMID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), anyMap(), anyList())).thenReturn(erBooking);
                                },
                                // Mock query roomDao
                                (Supplier) () -> {
                                    EntityResult erRoomDao = new EntityResultMapImpl();
                                    erRoomDao.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erRoomDao.put(RoomDao.IDROOM, List.of(1));
                                    erRoomDao.put(RoomDao.IDHOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(RoomDao.class), anyMap(), anyList())).thenReturn(erRoomDao);
                                },
                                // Mock query parkingHistoryDao
                                (Supplier) () -> {

                                    EntityResult erParkingHistory = new EntityResultMapImpl();
                                    erParkingHistory.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParkingHistory.put(ParkingHistoryDao.ENTRY_DATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erParkingHistory.put(ParkingHistoryDao.EXIT_DATE, new ArrayList<>());
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingHistoryDao.class), anyMap(), anyList())).thenReturn(erParkingHistory);
                                },
                                // Mock  insert parkingHistoryDao
                                (Supplier) () -> {
                                    EntityResult erParkingHistory = new EntityResultMapImpl();
                                    erParkingHistory.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(parkingHistoryService.parkingHistoryEnter(anyMap())).thenReturn(erParkingHistory);
                                },
                                // Mock  update parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.update(Mockito.any(ParkingDao.class), anyMap(), anyMap())).thenReturn(erParking);
                                }

                        )
                ),
                //endregion
                //region Test case 2: Parking not found
                Arguments.of(
                        "Parking not found",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.PARKING_NOT_FOUND
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                }
                        )
                ),
                //endregion
                //region Test case 3: Parking full
                Arguments.of(
                        "Parking full",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.PARKING_FULL
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(10));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                }
                        )
                ),
                //endregion
                //region Test case 4: Booking not found
                Arguments.of(
                        "Booking not found",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.BOOKING_NOT_EXIST
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(5));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                },
                                // Mock bookingDao
                                (Supplier) () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), anyMap(), anyList())).thenReturn(erBooking);
                                }

                        )
                ),
                //endregion
                //region Test case 5: Booking not checked in
                Arguments.of(
                        "Booking not checked in",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.BOOKING_NOT_CHECKED_IN
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(5));
                                    erParking.put(ParkingDao.ID_HOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                },
                                // Mock bookingDao
                                (Supplier) () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.STARTDATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ENDDATE, List.of(new Date(LocalDate.now().plusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ROOMID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), anyMap(), anyList())).thenReturn(erBooking);
                                }, // Mock query roomDao
                                (Supplier) () -> {
                                    EntityResult erRoomDao = new EntityResultMapImpl();
                                    erRoomDao.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erRoomDao.put(RoomDao.IDROOM, List.of(1));
                                    erRoomDao.put(RoomDao.IDHOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(RoomDao.class), anyMap(), anyList())).thenReturn(erRoomDao);
                                }
                        )
                ),
                //endregion
                //region Test case 6: Booking already checked out
                Arguments.of(
                        "Booking already checked out",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.BOOKING_ALREADY_CHECKED_OUT
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(5));
                                    erParking.put(ParkingDao.ID_HOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                },
                                // Mock bookingDao
                                (Supplier) () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.STARTDATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ENDDATE, List.of(new Date(LocalDate.now().plusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.CHECKIN_DATETIME, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.CHECKOUT_DATETIME, List.of(new Date(LocalDate.now().plusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ROOMID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), anyMap(), anyList())).thenReturn(erBooking);
                                }, // Mock query roomDao
                                (Supplier) () -> {
                                    EntityResult erRoomDao = new EntityResultMapImpl();
                                    erRoomDao.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erRoomDao.put(RoomDao.IDROOM, List.of(1));
                                    erRoomDao.put(RoomDao.IDHOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(RoomDao.class), anyMap(), anyList())).thenReturn(erRoomDao);
                                }
                        )
                ),
                //endregion
                //region Test case 7: Booking not started
                Arguments.of(
                        "Booking not started",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.BOOKING_NOT_STARTED
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(5));
                                    erParking.put(ParkingDao.ID_HOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                },
                                // Mock bookingDao
                                (Supplier) () -> {
                                    LocalDate tomorrow = LocalDate.now().plusDays(1);
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.STARTDATE, List.of(Date.from(tomorrow.atStartOfDay(ZoneId.systemDefault()).toInstant())));
                                    erBooking.put(BookingDao.ROOMID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), anyMap(), anyList())).thenReturn(erBooking);
                                },
                                // Mock query roomDao
                                (Supplier) () -> {
                                    EntityResult erRoomDao = new EntityResultMapImpl();
                                    erRoomDao.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erRoomDao.put(RoomDao.IDROOM, List.of(1));
                                    erRoomDao.put(RoomDao.IDHOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(RoomDao.class), anyMap(), anyList())).thenReturn(erRoomDao);
                                }
                        )
                ),
                //endregion
                //region Test case 8: Already in parking
                Arguments.of(
                        "Already in parking",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.BOOKING_ALREADY_IN_PARKING
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(5));
                                    erParking.put(ParkingDao.ID_HOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                },
                                // Mock bookingDao
                                (Supplier) () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.STARTDATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ENDDATE, List.of(new Date(LocalDate.now().plusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.CHECKIN_DATETIME, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ROOMID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), anyMap(), anyList())).thenReturn(erBooking);
                                },
                                // Mock query parkingHistoryDao
                                (Supplier) () -> {
                                    List<Object> entryDate = new ArrayList<>();
                                    entryDate.add(null);
                                    EntityResult erParkingHistory = new EntityResultMapImpl();
                                    erParkingHistory.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParkingHistory.put(ParkingHistoryDao.ENTRY_DATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erParkingHistory.put(ParkingHistoryDao.EXIT_DATE, entryDate);
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingHistoryDao.class), anyMap(), anyList())).thenReturn(erParkingHistory);
                                },
                                // Mock query roomDao
                                (Supplier) () -> {
                                    EntityResult erRoomDao = new EntityResultMapImpl();
                                    erRoomDao.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erRoomDao.put(RoomDao.IDROOM, List.of(1));
                                    erRoomDao.put(RoomDao.IDHOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(RoomDao.class), anyMap(), anyList())).thenReturn(erRoomDao);
                                }

                        )
                ),
                //endregion
                //region Test case 9: Not same id hotel booking and parking
                Arguments.of(
                        "Not same id hotel booking and parking",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.BOOKING_NOT_SAME_HOTEL_AS_PARKING
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(5));
                                    erParking.put(ParkingDao.ID_HOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                },
                                // Mock bookingDao
                                (Supplier) () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.STARTDATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ENDDATE, List.of(new Date(LocalDate.now().plusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.CHECKIN_DATETIME, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ROOMID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), anyMap(), anyList())).thenReturn(erBooking);
                                },
                                // Mock query roomDao
                                (Supplier) () -> {
                                    EntityResult erRoomDao = new EntityResultMapImpl();
                                    erRoomDao.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(RoomDao.class), anyMap(), anyList())).thenReturn(erRoomDao);
                                }

                        )
                ),
                //endregion
                //region Test case 10: Neccesary data
                Arguments.of(
                        "Neccesary data",
                        Map.of(),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.NECESSARY_DATA
                        ),
                        List.of()
                )
                //endregion


        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("parkingExitTestData")
    @DisplayName("TestParametrized parking Exit")
    void testParkingExit(String name, Map<String, Object> attr, EntityResult expectedResult, List<Supplier> mocks) {
        //For each mock, execute the get method, to make sure the mock is called
        mocks.forEach(mockMap -> {
            mockMap.get();
        });
        EntityResult result = parkingService.parkingExit(attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> parkingExitTestData() {
        return Stream.of(
                //region Test case 1: Successful enter
                Arguments.of(
                        "Successful enter",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_SUCCESSFUL,
                                ""
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(5));
                                    erParking.put(ParkingDao.ID_HOTEL, List.of(1));
                                    EntityResult erParking2 = new EntityResultMapImpl();
                                    erParking2.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking2.put(ParkingDao.PRICE, List.of(new BigDecimal(10)));
                                    erParking2.put(ParkingDao.DESCRIPTION, List.of("Parking"));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking, erParking2);
                                },
                                // Mock bookingDao
                                (Supplier) () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.STARTDATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ENDDATE, List.of(new Date(LocalDate.now().plusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.CHECKIN_DATETIME, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ROOMID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), anyMap(), anyList())).thenReturn(erBooking);
                                },
                                // Mock query parkingHistoryDao
                                (Supplier) () -> {

                                    EntityResult erParkingHistory = new EntityResultMapImpl();
                                    erParkingHistory.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParkingHistory.put(ParkingHistoryDao.ENTRY_DATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erParkingHistory.put(ParkingHistoryDao.EXIT_DATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erParkingHistory.put(ParkingHistoryDao.ID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingHistoryDao.class), anyMap(), anyList())).thenReturn(erParkingHistory);
                                },
                                // Mock  insert parkingHistoryDao
                                (Supplier) () -> {
                                    EntityResult erParkingHistory = new EntityResultMapImpl();
                                    erParkingHistory.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(parkingHistoryService.parkingHistoryExit(anyMap(),anyMap())).thenReturn(erParkingHistory);
                                },
                                // Mock  update parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.update(Mockito.any(ParkingDao.class), anyMap(), anyMap())).thenReturn(erParking);
                                },
                                // Mock  insert extraExpenseDao
                                (Supplier) () -> {
                                    EntityResult erExtraExpense = new EntityResultMapImpl();
                                    erExtraExpense.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(extraExpensesService.extraexpensesInsert(anyMap())).thenReturn(erExtraExpense);
                                }

                        )
                ),
                //endregion
                //region Test case 2: Parking not found
                Arguments.of(
                        "Parking not found",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.PARKING_NOT_FOUND
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                }
                        )
                ),
                //endregion
                //region Test case 4: Booking not found
                Arguments.of(
                        "Booking not found",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.BOOKING_NOT_EXIST
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(5));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                },
                                // Mock bookingDao
                                (Supplier) () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), anyMap(), anyList())).thenReturn(erBooking);
                                }

                        )
                ),
                //endregion
                //region Test case 8: Not in parking
                Arguments.of(
                        "Not in parking",
                        Map.of("id_booking", 1, "id_parking", 1),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.BOOKING_NOT_IN_PARKING
                        ),
                        List.of(
                                // Mock parkingDao
                                (Supplier) () -> {
                                    EntityResult erParking = new EntityResultMapImpl();
                                    erParking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParking.put(ParkingDao.TOTAL_CAPACITY, List.of(10));
                                    erParking.put(ParkingDao.OCCUPIED_CAPACITY, List.of(5));
                                    erParking.put(ParkingDao.ID_HOTEL, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingDao.class), anyMap(), anyList())).thenReturn(erParking);
                                },
                                // Mock bookingDao
                                (Supplier) () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.STARTDATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ENDDATE, List.of(new Date(LocalDate.now().plusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.CHECKIN_DATETIME, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erBooking.put(BookingDao.ROOMID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), anyMap(), anyList())).thenReturn(erBooking);
                                },
                                // Mock query parkingHistoryDao
                                (Supplier) () -> {
                                    List<Object> entryDate = new ArrayList<>();
                                    entryDate.add(null);
                                    EntityResult erParkingHistory = new EntityResultMapImpl();
                                    erParkingHistory.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erParkingHistory.put(ParkingHistoryDao.ENTRY_DATE, List.of(new Date(LocalDate.now().minusDays(1).toEpochDay()),new Date(LocalDate.now().minusDays(1).toEpochDay())));
                                    erParkingHistory.put(ParkingHistoryDao.EXIT_DATE, List.of(new Date(LocalDate.now().plusDays(1).toEpochDay()),new Date(LocalDate.now().plusDays(1).toEpochDay())));
                                    erParkingHistory.put(ParkingHistoryDao.ID, List.of(1,2));
                                    return Mockito.when(daoHelper.query(Mockito.any(ParkingHistoryDao.class), anyMap(), anyList())).thenReturn(erParkingHistory);
                                }

                        )
                ),
                //endregion
                //region Test case 10: Neccesary data
                Arguments.of(
                        "Neccesary data",
                        Map.of(),
                        Extras.createEntityResult(
                                EntityResult.OPERATION_WRONG,
                                Messages.NECESSARY_DATA
                        ),
                        List.of()
                )
                //endregion


        );
    }
}

