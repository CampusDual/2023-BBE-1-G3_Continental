package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.AccessCardDao;
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
class AccessCardServiceTest {
    @Mock
    static DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    AccessCardService accessCardService;
    @Mock
    static AccessCardDao accessCardDao;
    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("accessCardQuery")
    void testAccessCardQuery(String testCaseName, Map<?, ?> keyMap, List<?> attrList,EntityResult expectedResult,List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = accessCardService.accesscardQuery(keyMap, attrList);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }
    private static Stream<Arguments> accessCardQuery() {
        return Stream.of(
                // Test case 1: Successful query
                Arguments.of(
                        "Successful query",//Nombre del test
                        Map.of(),//keyMap
                        List.of(AccessCardDao.ACCESSCARDID),//attrList
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),//Resultado esperado
                        List.of(
                                (Supplier)() ->{
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erQuery.put(AccessCardDao.ACCESSCARDID, List.of(1));
                                    return when(daoHelper.query(any(AccessCardDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                })//Mocks
                ),
                // Test case 2: No columns to query
                Arguments.of(
                        "No columns to query",//Nombre del test
                        Map.of(),//keyMap
                        List.of(),//attrList
                        createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),//Resultado esperado
                        List.of()//Mocks
                ),
                // Test case 3: No access card found
                Arguments.of(
                        "No access card found",//Nombre del test
                        Map.of(AccessCardDao.ACCESSCARDID,1),//keyMap
                        List.of(AccessCardDao.ACCESSCARDID),//attrList
                        createEntityResult(EntityResult.OPERATION_WRONG, Messages.ACCESS_CARD_NOT_EXIST),//Resultado esperado
                        List.of(
                                (Supplier)() ->{
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return when(daoHelper.query(any(AccessCardDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                })//Mocks
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
