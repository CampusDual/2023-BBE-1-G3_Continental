package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.AccessCardAssignmentDao;
import com.hotel.continental.model.core.dao.AccessCardDao;
import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.ExtraExpensesDao;
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
public class ExtraExpensesServiceTest {
    @Mock
    static ExtraExpensesDao extraExpensesDao;
    @Mock
    static BookingDao bookingDao;
    @InjectMocks
    static ExtraExpensesService extraExpensesSrv;
    @Mock
    static DefaultOntimizeDaoHelper daoHelper;

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("extraExpensesInsert")
    void testExtraExpensesInsert(String testCaseName, Map<?, ?> attr, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = extraExpensesSrv.extraexpensesInsert(attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> extraExpensesInsert() {
        return Stream.of(
                //region Test case 1: Successful insert
                Arguments.of(
                        "Successful extra expenses",//Nombre del test
                        Map.of(ExtraExpensesDao.BOOKINGID, 2,
                                ExtraExpensesDao.CONCEPT, "concepto", ExtraExpensesDao.PRICE, 12),//keyMap
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erExpense = new EntityResultMapImpl();
                                    erExpense.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erExpense.put(ExtraExpensesDao.IDEXPENSE, List.of(1));
                                    erExpense.put(ExtraExpensesDao.BOOKINGID, List.of(2));
                                    erExpense.put(ExtraExpensesDao.CONCEPT, List.of("concepto"));
                                    erExpense.put(ExtraExpensesDao.PRICE, List.of(12));

                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erBooking.put(BookingDao.BOOKINGID, List.of(2));
                                    return List.of(Mockito.when(daoHelper.insert(Mockito.any(ExtraExpensesDao.class), Mockito.anyMap())).thenReturn(erExpense),
                                            Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erBooking));
                                }
                        )
                ),                //endregion
                //region Test case 2: Null id
                Arguments.of(
                        "null id",//Nombre del test
                        Map.of(1, ExtraExpensesDao.BOOKINGID),//keyMap
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_KEY),//Resultado esperado
                        List.of()
                ),
                //endregion
                //region Test case 3: Null data
                Arguments.of(
                        "null data",//Nombre del test
                        Map.of(ExtraExpensesDao.BOOKINGID, 1),//keyMap
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_DATA),//Resultado esperado
                        List.of()
                ),
                //endregion
                //region Test case 4: Empty Data
                Arguments.of(
                        "empty data",//Nombre del test
                        Map.of(ExtraExpensesDao.BOOKINGID, 1, ExtraExpensesDao.CONCEPT, "", ExtraExpensesDao.PRICE, ""),//keyMap
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_DATA),//Resultado esperado
                        List.of()
                ),
                //endregion
                //region Test case 5: Booking doesn´t exist
                Arguments.of(
                        "Booking doesn´t exist",//Nombre del test
                        Map.of(ExtraExpensesDao.BOOKINGID, 3,
                                ExtraExpensesDao.CONCEPT, "concepto", ExtraExpensesDao.PRICE, 12),//keyMap
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.BOOKING_NOT_EXIST),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erBooking = new EntityResultMapImpl();
                                    erBooking.setCode(EntityResult.OPERATION_WRONG);
                                    return List.of(Mockito.when(daoHelper.query(Mockito.any(BookingDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erBooking));
                                }
                        )
                )
                //endregion
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
