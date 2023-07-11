package com.hotel.continental.model.core.service;


import com.hotel.continental.model.core.dao.CriteriaDao;
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
class CriteriaTest {
    @Mock
    private static DefaultOntimizeDaoHelper daoHelper;
    @Mock
    private CriteriaDao criteriaDao;
    @InjectMocks
    private CriteriaService criteriaService;


    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("criteriaQuery")
    void testcriteriaQuery(String testCaseName, Map<?, ?> keyMap, List<String> attrList, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = criteriaService.criteriaQuery(keyMap, attrList);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> criteriaQuery() {
        return Stream.of(
                //region Test case 1: Successful criteriaQuery without filters
                Arguments.of(
                        "Successful criteriaQuery without filters",//Nombre del test
                        Map.of(),//keyMap
                        List.of(),//attrList
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erCriteria = new EntityResultMapImpl();
                                    return Mockito.when(daoHelper.query(Mockito.any(CriteriaDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erCriteria);
                                }
                        )
                ),
                //endregion
                //region Test case 2: Successful criteriaQuery with filters
                Arguments.of(
                        "Successful criteriaQuery with filters",//Nombre del test
                        Map.of(CriteriaDao.ID,1),//keyMap
                        List.of(),//attrList
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erCriteria = new EntityResultMapImpl();
                                    erCriteria.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    erCriteria.put(CriteriaDao.ID,List.of(1));
                                    return Mockito.when(daoHelper.query(Mockito.any(CriteriaDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erCriteria);
                                }
                        )
                ),
                //endregion
                //region Test case 3: No criteriaQuery with filters
                Arguments.of(
                        "Successful criteriaQuery with filters",//Nombre del test
                        Map.of(CriteriaDao.ID,1),//keyMap
                        List.of(),//attrList
                        createEntityResult(EntityResult.OPERATION_WRONG, Messages.CRITERIA_NOT_EXIST),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erCriteria = new EntityResultMapImpl();
                                    erCriteria.setCode(EntityResult.OPERATION_SUCCESSFUL);
                                    return Mockito.when(daoHelper.query(Mockito.any(CriteriaDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erCriteria);
                                }
                        )
                )
                //endregion

        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("criteriaUpdate")
    void testcriteriaUpdate(String testCaseName, Map<?, ?> keyMap, Map<String, Object> attrList, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = criteriaService.criteriaUpdate(attrList, keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> criteriaUpdate() {
        return Stream.of(
                //region Test case 1: Successful criteriaUpdate
                Arguments.of(
                        "Successful criteriaUpdate",//Nombre del test
                        Map.of(CriteriaDao.ID, 1),//keyMap
                        Map.of(CriteriaDao.DESCRIPTION, "description",
                                CriteriaDao.NAME, "criteriaUpdate",
                                CriteriaDao.MULTIPLIER, 1.2),//attrList
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erCriteriaQuery = new EntityResultMapImpl();
                                    erCriteriaQuery.put(CriteriaDao.ID, List.of(1));

                                    return Mockito.when(daoHelper.query(Mockito.any(CriteriaDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erCriteriaQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erCriteria = new EntityResultMapImpl();
                                    erCriteria.put(CriteriaDao.ID, List.of(1));
                                    erCriteria.put(CriteriaDao.DESCRIPTION, List.of("description"));
                                    erCriteria.put(CriteriaDao.MULTIPLIER, List.of(1.2));
                                    erCriteria.put(CriteriaDao.NAME, List.of("criteriaUpdate"));

                                    return Mockito.when(daoHelper.update(Mockito.any(CriteriaDao.class), Mockito.anyMap(), Mockito.anyMap())).thenReturn(erCriteria);
                                }
                        )
                ),
                //endregion
                //region Test case 2: criteriaUpdate with wrong attrMap
                Arguments.of(
                        "criteriaQuery with wrong attrMap",//Nombre del test
                        Map.of(CriteriaDao.ID,1),//keyMap
                        Map.of(),//attrList
                        createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),//Resultado esperado
                        List.of()
                ),
                //endregion
                //region Test case 3: criteriaUpdate with wrong attrMap
                Arguments.of(
                        "criteriaQuery with wrong keyMap",//Nombre del test
                        Map.of(),//keyMap
                        Map.of(CriteriaDao.DESCRIPTION, "description"),//attrList
                        createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),//Resultado esperado
                        List.of()
                ),
                //endregion
                //region Test case 4: criteriaUpdate with wrong multiplier(not number)
                Arguments.of(
                        "criteriaQuery with wrong multiplier(not number)",//Nombre del test
                        Map.of(CriteriaDao.ID, 1),//keyMap
                        Map.of(CriteriaDao.DESCRIPTION, "description",
                                CriteriaDao.NAME, "criteriaUpdate",
                                CriteriaDao.MULTIPLIER, "a"),//attrList
                        createEntityResult(EntityResult.OPERATION_WRONG, Messages.MULTIPLIER_NOT_NUMBER),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erCriteriaQuery = new EntityResultMapImpl();
                                    erCriteriaQuery.put(CriteriaDao.ID, List.of(1));

                                    return Mockito.when(daoHelper.query(Mockito.any(CriteriaDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erCriteriaQuery);
                                }
                        )
                ),
                //endregion
                //region Test case 5: criteriaUpdate with wrong multiplier(not number)
                Arguments.of(
                        "criteriaQuery with wrong multiplier(bad format)",//Nombre del test
                        Map.of(CriteriaDao.ID, 1),//keyMap
                        Map.of(CriteriaDao.DESCRIPTION, "description",
                                CriteriaDao.NAME, "criteriaUpdate",
                                CriteriaDao.MULTIPLIER, -1),//attrList
                        createEntityResult(EntityResult.OPERATION_WRONG, Messages.MULTIPLIER_NOT_POSITIVE),//Resultado esperado
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erCriteriaQuery = new EntityResultMapImpl();
                                    erCriteriaQuery.put(CriteriaDao.ID, List.of(1));

                                    return Mockito.when(daoHelper.query(Mockito.any(CriteriaDao.class), Mockito.anyMap(), Mockito.anyList())).thenReturn(erCriteriaQuery);
                                }
                        )
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
