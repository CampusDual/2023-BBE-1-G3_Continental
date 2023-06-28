package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.AccessCardAssignmentDao;
import com.hotel.continental.model.core.dao.AccessCardDao;
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

@ExtendWith(MockitoExtension.class)
public class AccessCardAssignmentServiceTest {
    @Mock
    static DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    AccessCardAssignmentService accessCardAssignmentServiceService;
    @Mock
    static AccessCardDao AccessCardDao;
    @Mock
    static AccessCardAssignmentDao accessCardAssignmentDao;

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
                //<editor-fold desc="Test case 1: Successful accessCardCheckout">
                Arguments.of(
                        "Successful accessCardCheckout",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESSCARDID, 1,AccessCardAssignmentDao.BOOKINGID,1),//keyMap
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ErrorMessages.ACCESS_CARD_RECOVERED),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjeta.put(AccessCardDao.AVALIABLE, List.of(true));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                },
                                (Supplier) () -> {
                                    EntityResult erTarjetaAsignada = new EntityResultMapImpl();
                                    erTarjetaAsignada.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjetaAsignada.put(AccessCardDao.AVALIABLE, List.of(true));
                                    erTarjetaAsignada.put(AccessCardAssignmentDao.BOOKINGID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardAssignmentDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjetaAsignada);
                                },
                                (Supplier) () -> {
                                    EntityResult erUpdate = new EntityResultMapImpl();
                                    erUpdate.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.update(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(erUpdate);
                                }
                        )
                ),
                //</editor-fold>
                //<editor-fold desc="Test case 2: Not accessCard id">
                Arguments.of(
                        "No accessCard id",//Nombre del test
                        Map.of( 1,AccessCardAssignmentDao.BOOKINGID),//keyMap
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_KEY),//Resultado esperado
                        List.of()
                ),
                //</editor-fold>
                //<editor-fold desc="Test case 3: Not booking id">
                Arguments.of(
                        "No booking id",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESSCARDID, 1),//keyMap
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_KEY),//Resultado esperado
                        List.of()
                ),
                //</editor-fold>
                //<editor-fold desc="Test case 4: Not Exist accessCard id">
                Arguments.of(
                        "No Exist accessCard id",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESSCARDID, 1,AccessCardAssignmentDao.BOOKINGID,1),//keyMap
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.ACCESS_CARD_NOT_EXIST),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                }
                        )
                ),
                //</editor-fold>
                //<editor-fold desc="Test case 5: Not asigned accessCard id">
                Arguments.of(
                        "Already asigned accessCard id",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESSCARDID, 1,AccessCardAssignmentDao.BOOKINGID,1),//keyMap
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.ACCESS_CARD_ALREADY_GIVEN),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjeta.put(AccessCardDao.AVALIABLE, List.of(false));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                }
                        )
                ),
                //</editor-fold>
                //<editor-fold desc="Test case 6: Different booking on assignment and accessCard">
                Arguments.of(
                        "Different booking on assignment and accessCard",//Nombre del test
                        Map.of(AccessCardAssignmentDao.ACCESSCARDID, 1,AccessCardAssignmentDao.BOOKINGID,1),//keyMap
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ErrorMessages.ACCESS_CARD_RECOVERED),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erTarjeta = new EntityResultMapImpl();
                                    erTarjeta.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjeta.put(AccessCardDao.AVALIABLE, List.of(true));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjeta);
                                },
                                (Supplier) () -> {
                                    EntityResult erTarjetaAsignada = new EntityResultMapImpl();
                                    erTarjetaAsignada.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erTarjetaAsignada.put(AccessCardAssignmentDao.BOOKINGID, List.of(2));
                                    erTarjetaAsignada.put(AccessCardDao.ACCESSCARDID, List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(AccessCardAssignmentDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erTarjetaAsignada);
                                },
                                (Supplier) () -> {
                                    EntityResult erUpdate = new EntityResultMapImpl();
                                    erUpdate.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.update(Mockito.any(AccessCardDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(erUpdate);
                                }
                        )
                )
                //</editor-fold>

        );
    }

    private static EntityResult createEntityResult(int code, String message) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(code);
        er.setMessage(message);
        return er;
    }
}
