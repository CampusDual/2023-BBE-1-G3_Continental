package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.AccessCardAssignmentDao;
import com.hotel.continental.model.core.service.AccessCardAssignmentService;
import com.hotel.continental.model.core.dao.AccessCardDao;
import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.RoomDao;
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
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
class AccessCardAssignmentServiceTest {
    @Mock
    static DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    AccessCardAssignmentService accessCardAssignmentServiceService;
    @Mock
    static AccessCardDao accessCardDao;
    @Mock
    static AccessCardAssignmentDao accessCardAssignmentDao;
    @Mock
    static BookingDao bookingDao;
    @Mock
    static RoomDao roomDao;

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("accessCardCheckIn")
    void testaccessCardCheckIn(String testCaseName, Map<String, Object> attr, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = accessCardAssignmentServiceService.accesscardassignmentInsert(attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> accessCardCheckIn() {
        return Stream.of(
                //region Test case 1: Successful accessCardCheckIn
                Arguments.of(
                        "Successful accessCardCheckout",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1, AccessCardAssignmentDao.BOOKING_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, "The card 1 was given"),//Resultado esperado
                        List.of(
                                () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjeta.put(AccessCardDao.AVAILABLE, List.of(true));
                                    erTarjeta.put(AccessCardDao.ACCESS_CARD_ID, List.of(1));
                                    erTarjeta.put(AccessCardDao.HOTEL_ID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                },
                                () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.ROOMID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erBooking);
                                },
                                () -> {
                                    EntityResult erRoomHotel = new EntityResultMapImpl();
                                    erRoomHotel.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erRoomHotel.put(RoomDao.HOTEL_ID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erRoomHotel);
                                },
                                () -> {
                                    EntityResult erUpdate = new EntityResultMapImpl();
                                    erUpdate.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.update(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(erUpdate);
                                },
                                (Supplier) () -> {
                                    EntityResult erInsert = new EntityResultMapImpl();
                                    erInsert.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.insert(Mockito.any(AccessCardAssignmentDao.class), Mockito.anyMap())).thenReturn(erInsert);
                                }
                        )
                ),
                //endregion
                //region Test case 2 : No accessCard id
                Arguments.of(
                        "No accessCard id",//Nombre del test
                        Map.of(1, AccessCardAssignmentDao.BOOKING_ID),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),//Resultado esperado
                        List.of()
                ),
                //endregion
                //region Test case 3: Not booking id
                Arguments.of(
                        "No booking id",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),//Resultado esperado
                        List.of()
                ),
                //endregion
                //region Test case 4: Not Exist accessCard id
                Arguments.of(
                        "No Exist accessCard id",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1, AccessCardAssignmentDao.BOOKING_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ACCESS_CARD_NOT_EXIST),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                }
                        )
                ),
                //endregion
                //region Test case 5: Already asigned accessCard id
                Arguments.of(
                        "Already asigned accessCard id",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1, AccessCardAssignmentDao.BOOKING_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ACCESS_CARD_ALREADY_GIVEN),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjeta.put(AccessCardDao.AVAILABLE, List.of(false));
                                    EntityResult erCardAvailable = new EntityResultMapImpl();
                                    erCardAvailable.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erCardAvailable.put(AccessCardDao.AVAILABLE, List.of(true));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta, erCardAvailable);
                                }
                        )
                ),
                //endregion
                //region Test case 6: Different booking on assignment and accessCard
                Arguments.of(
                        "Different booking on assignment and accessCard",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1, AccessCardAssignmentDao.BOOKING_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.HOTEL_INCORRECT),//Resultado esperado
                        List.of(
                                () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjeta.put(AccessCardDao.AVAILABLE, List.of(true));
                                    EntityResult erCardAvailable = new EntityResultMapImpl();
                                    erCardAvailable.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    EntityResult erCardHotel = new EntityResultMapImpl();
                                    erCardHotel.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erCardHotel.put(RoomDao.HOTEL_ID, List.of(2));
                                    EntityResult eQuery = new EntityResultMapImpl();
                                    eQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta, erCardAvailable, erCardHotel, eQuery);
                                },
                                () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.ROOMID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erBooking);
                                },
                                (Supplier) () -> {
                                    EntityResult erRoomHotel = new EntityResultMapImpl();
                                    erRoomHotel.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erRoomHotel.put(RoomDao.HOTEL_ID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(RoomDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erRoomHotel);
                                }
                        )
                )
                //endregion

        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("accessCardCheckOut")
    void testaccessCardCheckOut(String testCaseName, Map<?, ?> attr, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = accessCardAssignmentServiceService.accesscardassignmentRecover(attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> accessCardCheckOut() {
        return Stream.of(
                //region Test case 1: Successful accessCardCheckout
                Arguments.of(
                        "Successful accessCardCheckout",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1, AccessCardAssignmentDao.BOOKING_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),//Resultado esperado
                        List.of(
                                () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjeta.put(AccessCardDao.AVAILABLE, List.of(false));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                },
                                () -> {
                                    EntityResult erTarjetaAsignada = new EntityResultMapImpl();
                                    erTarjetaAsignada.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjetaAsignada.put(AccessCardAssignmentDao.ACCESS_CARD_ID, List.of(1));
                                    erTarjetaAsignada.put(AccessCardAssignmentDao.BOOKING_ID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardAssignmentDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjetaAsignada);
                                },
                                (Supplier) () -> {
                                    EntityResult erUpdate = new EntityResultMapImpl();
                                    erUpdate.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.update(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(erUpdate);
                                }
                        )
                ),
                //endregion
                //region Test case 2: Not accessCard id
                Arguments.of(
                        "No accessCard id",//Nombre del test
                        Map.of(1, AccessCardAssignmentDao.BOOKING_ID),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),//Resultado esperado
                        List.of()
                ),
                //endregion
                //region Test case 3: Not Exist accessCard id
                Arguments.of(
                        "No Exist accessCard id",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1, AccessCardAssignmentDao.BOOKING_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ACCESS_CARD_NOT_EXIST),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                }
                        )
                ),
                //endregion
                //region Test case 4: Not asigned accessCard id
                Arguments.of(
                        "Already asigned accessCard id",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1, AccessCardAssignmentDao.BOOKING_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ACCESS_CARD_NOT_GIVEN),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjeta.put(AccessCardDao.AVAILABLE, List.of(true));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                }
                        )
                ),
                //endregion
                //region Test case 5: Different booking on assignment and accessCard
                Arguments.of(
                        "Different booking on assignment and accessCard",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1, AccessCardAssignmentDao.BOOKING_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.CARD_DOESNT_BELONG_BOOKING),//Resultado esperado
                        List.of(
                                () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjeta.put(AccessCardDao.AVAILABLE, List.of(false));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                },
                                (Supplier) () -> {
                                    EntityResult erTarjetaAsignada = new EntityResultMapImpl();
                                    erTarjetaAsignada.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardAssignmentDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjetaAsignada);
                                }
                        )
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("lostCard")
    void testLostCard(String testCaseName, Map<?, ?> attr, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = accessCardAssignmentServiceService.lostCard((Map<String, Object>) attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> lostCard() {
        return Stream.of(
                //region Test case 1: Successful lostCard
                Arguments.of(
                        "Successful lostCard",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, Messages.ACCESS_CARD_SUCCESSFULLY_MODIFY),//Resultado esperado
                        List.of(
                                () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.put(AccessCardAssignmentDao.ACCESS_CARD_ASSIGNMENT_ID, List.of(25));

                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardAssignmentDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                },
                                () -> {
                                    EntityResult erTarjetaUpdate = new EntityResultMapImpl();
                                    erTarjetaUpdate.put(AccessCardDao.ACCESS_CARD_ID, List.of(1));

                                    return Mockito.when(daoHelper.delete(Mockito.any(AccessCardAssignmentDao.class), Mockito.anyMap())).thenReturn(erTarjetaUpdate);
                                },
                                (Supplier) () -> {
                                    EntityResult erTarjetaUpdate = new EntityResultMapImpl();
                                    erTarjetaUpdate.put(AccessCardAssignmentDao.ACCESS_CARD_ASSIGNMENT_ID, List.of(25));

                                    return Mockito.when(daoHelper.update(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(erTarjetaUpdate);
                                }
                        )
                ),
                //endregion
                //region Test case 2: Not accessCard id
                Arguments.of(
                        "No accessCard id",//Nombre del test
                        Map.of(1, AccessCardAssignmentDao.BOOKING_ID),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),//Resultado esperado
                        List.of()
                ),
                //endregion
                //region Test case 3: Not Exist accessCard id
                Arguments.of(
                        "No Exist accessCard id",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ACCESS_CARD_NOT_RECOVERED),//Resultado esperado
                        List.of(
                                () -> {
                                    EntityResult erTarjetaUpdate = new EntityResultMapImpl();
                                    erTarjetaUpdate.setCode(EntityResult.OPERATION_WRONG);
                                    return Mockito.when(daoHelper.update(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(erTarjetaUpdate);
                                },
                                () -> {
                                    EntityResult erTarjetaUpdate = new EntityResultMapImpl();
                                    erTarjetaUpdate.put(AccessCardDao.ACCESS_CARD_ID, List.of(1));

                                    return Mockito.when(daoHelper.delete(Mockito.any(AccessCardAssignmentDao.class), Mockito.anyMap())).thenReturn(erTarjetaUpdate);
                                },
                                (Supplier) () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.put(AccessCardAssignmentDao.ACCESS_CARD_ASSIGNMENT_ID, List.of(25));

                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardAssignmentDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                }
                        )
                ),
                //endregion
                //region Test case 4: Accesscardassignment relation not found
                Arguments.of(
                        "Accesscardassignment relation not found",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESS_CARD_ID, 1),//keyMap
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ACCESS_CARD_NOT_RECOVERED),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_WRONG);
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardAssignmentDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                }
                        )
                )
                //endregion
        );
    }
}
