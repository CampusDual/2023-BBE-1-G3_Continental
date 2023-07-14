package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.ClientDao;
import com.hotel.continental.model.core.dao.RoleDao;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {
    @Mock
    static
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    ClientService clientService;

    @Mock
    ClientDao clientDao;

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("clientInsert")
    void testClientInsert(String testCaseName, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = clientService.clientInsert(attrMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> clientInsert() {

        return Stream.of(
                //region Test Case 1 - Insert client with correct data
                Arguments.of(
                        "Insert client with correct data",
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "ES", "52033014E")),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, "Cliente insertado correctamente"),
                        List.of(
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.setCode(EntityResult.OPERATION_WRONG);

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));
                                    erQuery.put(ClientDao.NAME, List.of("name"));

                                    return when(daoHelper.insert(any(ClientDao.class), anyMap())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Insert client with null data
                Arguments.of(
                        "Insert client with null data",
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endregion
                //region Test Case 3 - Insert client with empty data
                Arguments.of(
                        "Insert client with empty data",
                        Map.of(ClientDao.NAME, ""),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endRegion
                //region Test Case 4 - Insert client document not valid
                Arguments.of(
                        "Insert client document not valid",
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "ES", "52033014F")),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.DOCUMENT_NOT_VALID),
                        List.of()
                ),
                //endRegion
                //region Test Case 5 - Insert client countryCode format error
                Arguments.of(
                        "Insert client CountryCode format error",
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "ESPAÑA", "52033014E")),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.COUNTRY_CODE_FORMAT_ERROR),
                        List.of()
                ),
                //endRegion
                //region Test Case 6 - Insert client countryCode not exist
                Arguments.of(
                        "Insert client CountryCode not valid",
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "AA", "52033014E")),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.COUNTRY_CODE_NOT_VALID),
                        List.of()
                ),
                //endRegion
                //region Test Case 7 - Insert client client already exist
                Arguments.of(
                        "Insert client client already exist",
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "ES", "52033014E")),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.CLIENT_ALREADY_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));
                                    erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("clientUpdate")
    void testClientUpdate(String testCaseName, Map<String, Object> keyMap, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = clientService.clientUpdate(attrMap, keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> clientUpdate() {

        return Stream.of(
                //region Test Case 1 - Update client with correct data
                Arguments.of(
                        "Update client with correct data",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "ES", "52033014E")),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));

                                    EntityResult erQueryNotExist = new EntityResultMapImpl();

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery, erQueryNotExist);
                                },
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));
                                    erQuery.put(ClientDao.NAME, List.of("name"));

                                    return when(daoHelper.update(any(ClientDao.class), anyMap(), anyMap())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Update client with null key
                Arguments.of(
                        "Update client with null key",
                        Map.of(),
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),
                        List.of()
                ),
                //endregion
                //region Test Case 3 - Update client with null data
                Arguments.of(
                        "Update client with null data",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endRegion
                //region Test Case 4 - Update client document not valid
                Arguments.of(
                        "Update client document not valid",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "ES", "52033014F")),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.DOCUMENT_NOT_VALID),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endRegion
                //region Test Case 5 - Update client countryCode format error
                Arguments.of(
                        "Update client CountryCode format error",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "ESPAÑA", "52033014E")),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.COUNTRY_CODE_FORMAT_ERROR),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                })
                ),
                //endRegion
                //region Test Case 6 - Update client countryCode not exist
                Arguments.of(
                        "Update client CountryCode not valid",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "AA", "52033014E")),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.COUNTRY_CODE_NOT_VALID),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                })
                ),
                //endRegion
                //region Test Case 7 - Update client client already exist
                Arguments.of(
                        "Update client client already exist",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "ES", "52033014E")),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.CLIENT_ALREADY_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));
                                    erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endRegion
                //region Test Case 8 - Update client not exist
                Arguments.of(
                        "Update client client not exist",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        constructMap(List.of(ClientDao.NAME, ClientDao.COUNTRY_CODE, ClientDao.DOCUMENT),
                                List.of("name", "ES", "52033014E")),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.CLIENT_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("clientDelete")
    void testClientDelete(String testCaseName, Map<String, Object> keyMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = clientService.clientDelete(keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> clientDelete() {

        return Stream.of(
                //region Test Case 1 - Delete client with correct data
                Arguments.of(
                        "Delete client with correct data",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, "Este cliente se ha dado de baja con fecha " + new SimpleDateFormat("yyyy-MM-dd").format(new Date())),
                        List.of(
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));

                                    EntityResult erQueryNotExist = new EntityResultMapImpl();

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery, erQueryNotExist);
                                },
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoleDao.ID_ROLENAME, List.of(1));
                                    erQuery.put(RoleDao.ROLENAME, List.of("name"));
                                    erQuery.put(ClientDao.CLIENT_DOWN_DATE, List.of(new Date()));

                                    return when(daoHelper.update(any(ClientDao.class), anyMap(), anyMap())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Delete client with null key
                Arguments.of(
                        "Delete Role with null key",
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),
                        List.of()
                ),
                //endRegion
                //region Test Case 4 - Delete client not exist
                Arguments.of(
                        "Delete client not exist",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.CLIENT_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endRegion
                //region Test Case 5 - Delete client already deleted
                Arguments.of(
                        "Delete client already deleted",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.CLIENT_ALREADY_DELETED),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));
                                    erQuery.put(ClientDao.CLIENT_DOWN_DATE, List.of(new Date()));

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                })
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("clientQuery")
    void testClientQuery(String testCaseName, Map<String, Object> keyMap, List<String> attrList, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = clientService.clientQuery(keyMap, attrList);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> clientQuery() {

        return Stream.of(
                //region Test Case 1 - Query client with filter
                Arguments.of(
                        "query client with filter",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        List.of(ClientDao.CLIENT_ID, ClientDao.NAME, ClientDao.COUNTRY_CODE),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));
                                    erQuery.put(ClientDao.NAME, List.of("name"));
                                    erQuery.put(ClientDao.COUNTRY_CODE, List.of("ES"));

                                    EntityResult erQueryNotExist = new EntityResultMapImpl();

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery, erQueryNotExist);
                                }
                        )
                ),
                //region Test Case 1 - Delete client without filter
                Arguments.of(
                        "Delete client without filter",
                        Map.of(),
                        List.of(ClientDao.CLIENT_ID, ClientDao.NAME, ClientDao.COUNTRY_CODE),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(ClientDao.CLIENT_ID, List.of(1));
                                    erQuery.put(ClientDao.NAME, List.of("name"));
                                    erQuery.put(ClientDao.COUNTRY_CODE, List.of("ES"));

                                    EntityResult erQueryNotExist = new EntityResultMapImpl();

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery, erQueryNotExist);
                                }
                        )
                ),
                //endregion
                //region Test Case 3 - Delete client with null data
                Arguments.of(
                        "Delete Role with null data",
                        Map.of(),
                        List.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endRegion
                //region Test Case 4 - Delete client not exist
                Arguments.of(
                        "Delete client not exist",
                        Map.of(ClientDao.CLIENT_ID, 1),
                        List.of(ClientDao.CLIENT_ID, ClientDao.NAME, ClientDao.COUNTRY_CODE),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.CLIENT_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                )
                //endregion
        );
    }

    private static Map<String, Object> constructMap(List<String> indexes, List<Object> values) {
        Map<String, Object> map = new HashMap<>();
        for (int i = 0; i < indexes.size(); i++) {
            map.put(indexes.get(i), values.get(i));
        }
        return map;
    }
}