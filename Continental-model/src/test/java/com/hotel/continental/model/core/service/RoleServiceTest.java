package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.RoleDao;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class RoleServiceTest {
    //Generame los test para el metodo roleQuery
    @Mock
    static
    DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    RoleService roleService;

    @Mock
    RoleDao roleDao;

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("roleQuery")
    void testRoleQuery(String testCaseName, Map<String, Object> keyMap, List<String> attrList,EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = roleService.roleQuery(keyMap, attrList);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> roleQuery() {
        return Stream.of(
                //region Test Case 1 - Query Role with correct data with filter
                Arguments.of(
                        "Query Role with correct data",
                        Map.of(RoleDao.ID_ROLENAME, 1),
                        List.of(RoleDao.ID_ROLENAME, RoleDao.ROLENAME),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoleDao.ID_ROLENAME, List.of(1));
                                    erQuery.put(RoleDao.ROLENAME, List.of("roleName"));

                                    return when(daoHelper.query(Mockito.any(RoleDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endRegion
                //region Test Case 2 - Query Role with correct data without filter
                Arguments.of(
                        "Query Role with correct data",
                        Map.of(),
                        List.of(RoleDao.ID_ROLENAME, RoleDao.ROLENAME),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoleDao.ID_ROLENAME, List.of(1));
                                    erQuery.put(RoleDao.ROLENAME, List.of("roleName"));

                                    return when(daoHelper.query(Mockito.any(RoleDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 3 - Query Role with null data
                Arguments.of(
                        "Query Role with null data",
                        Map.of(),
                        List.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endregion
                //region Test Case 3 - Query Role with role not exist
                Arguments.of(
                        "Query Role with role not exist",
                        Map.of(),
                        List.of(RoleDao.ID_ROLENAME, RoleDao.ROLENAME),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROLE_DOESNT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(Mockito.any(RoleDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("roleInsert")
    void testRoleInsert(String testCaseName, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = roleService.roleInsert(attrMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> roleInsert() {
        Map<String, Object> attrMap = new HashMap<>();
        attrMap.put(RoleDao.ROLENAME, "name");
        return Stream.of(
                //region Test Case 1 - Insert Role with correct data
                Arguments.of(
                        "Insert Role with correct data",
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(Mockito.any(RoleDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                },
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoleDao.ID_ROLENAME, List.of(1));
                                    erQuery.put(RoleDao.ROLENAME, List.of("name"));

                                    return when(daoHelper.insert(Mockito.any(RoleDao.class), Mockito.anyMap())).thenReturn(erQuery);
                                }
                        )
                ),
                //endRegion
                //region Test Case 2 - Insert Role with existed role
                Arguments.of(
                        "Insert Role with correct data",
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROLE_ALREADY_EXISTS),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoleDao.ID_ROLENAME, List.of(1));
                                    erQuery.put(RoleDao.ROLENAME, List.of("roleName"));

                                    return when(daoHelper.query(Mockito.any(RoleDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 3 - Insert Role with null data
                Arguments.of(
                        "Insert Role with null data",
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                )
                //endregion
        );
    }


    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("roleDelete")
    void testRoleDelete(String testCaseName, Map<String, Object> keyMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = roleService.roleDelete(keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> roleDelete() {
        int roleId = 1;
        return Stream.of(
                //region Test Case 1 - Delete Role with correct data
                Arguments.of(
                        "Delete Role with correct data",
                        Map.of(RoleDao.ID_ROLENAME, roleId),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, "Role " + roleId + " deleted successfully"),
                        List.of(
                                () -> {
                                    EntityResult erQueryDoesntExist = new EntityResultMapImpl();

                                    EntityResult erQueryExist = new EntityResultMapImpl();
                                    erQueryExist.put(RoleDao.ID_ROLENAME, List.of(roleId));
                                    erQueryExist.put(RoleDao.ROLENAME, List.of("roleName"));

                                    return when(daoHelper.query(Mockito.any(RoleDao.class), Mockito.anyMap(), anyList())).thenReturn(erQueryExist, erQueryDoesntExist);
                                },
                                (Supplier) () -> {
                                    EntityResult erDelete = new EntityResultMapImpl();
                                    erDelete.put(RoleDao.ID_ROLENAME, List.of(roleId));
                                    erDelete.put(RoleDao.ROLENAME, List.of("NameUpdate"));

                                    return when(daoHelper.delete(Mockito.any(RoleDao.class), Mockito.anyMap())).thenReturn(erDelete);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Delete Role with null key
                Arguments.of(
                        "Delete Role with null key",
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),
                        List.of()
                ),
                //endRegion
                //region Test Case 3 - Delete Role with role not exist
                Arguments.of(
                        "Delete Role with role not exist",
                        Map.of(RoleDao.ID_ROLENAME, 0),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ADMIN_ROLE_NOT_EDITABLE),
                        List.of()
                ),
                //endregion
                //region Test Case 4 - Delete Role with role doesnt exist
                Arguments.of(
                        "Delete Role with role doesnt exist",
                        Map.of(RoleDao.ID_ROLENAME, roleId),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROLE_DOESNT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();

                                    return when(daoHelper.query(Mockito.any(RoleDao.class), Mockito.anyMap(), anyList())).thenReturn(erQuery);
                                }
                        )
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("roleUpdate")
    void testRoleUpdate(String testCaseName, Map<String, Object> keyMap, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = roleService.roleUpdate(attrMap, keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> roleUpdate() {
        int roleId = 1;
        Map<String, Object> attrMap = new HashMap<>();
        attrMap.put(RoleDao.ROLENAME, "nameUpdate");
        return Stream.of(
                //region Test Case 1 - Delete Role with correct data
                Arguments.of(
                        "Delete Role with correct data",
                        Map.of(RoleDao.ID_ROLENAME, roleId),
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, "Role " + roleId + " updated successfully"),
                        List.of(
                                () -> {
                                    EntityResult erQueryDoesntExist = new EntityResultMapImpl();

                                    EntityResult erQueryExist = new EntityResultMapImpl();
                                    erQueryExist.put(RoleDao.ID_ROLENAME, List.of(roleId));
                                    erQueryExist.put(RoleDao.ROLENAME, List.of("roleName"));

                                    return when(daoHelper.query(Mockito.any(RoleDao.class), Mockito.anyMap(), anyList())).thenReturn(erQueryExist, erQueryDoesntExist);
                                },
                                (Supplier) () -> {
                                    EntityResult erQuery = new EntityResultMapImpl();
                                    erQuery.put(RoleDao.ID_ROLENAME, List.of(roleId));
                                    erQuery.put(RoleDao.ROLENAME, List.of("NameUpdate"));

                                    return when(daoHelper.update(Mockito.any(RoleDao.class), Mockito.anyMap(), anyMap())).thenReturn(erQuery);
                                }
                        )
                ),
                //endregion
                //region Test Case 2 - Delete Role with null key
                Arguments.of(
                        "Delete Role with null key",
                        Map.of(),
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),
                        List.of()
                ),
                //endregion
                //region Test Case 3 - Delete Role with null data
                Arguments.of(
                        "Delete Role with null data",
                        Map.of(RoleDao.ID_ROLENAME, roleId),
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endRegion
                //region Test Case 4 - Delete Role with role not exist
                Arguments.of(
                        "Delete Role with role not exist",
                        Map.of(RoleDao.ID_ROLENAME, roleId),
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROLE_DOESNT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryDoesntExist = new EntityResultMapImpl();

                                    return when(daoHelper.query(Mockito.any(RoleDao.class), Mockito.anyMap(), anyList())).thenReturn(erQueryDoesntExist);
                                }
                        )
                ),
                //endregion
                //region Test Case 5 - Query Role with role already exist
                Arguments.of(
                        "Query Role with role already exist",
                        Map.of(RoleDao.ID_ROLENAME, roleId),
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.ROLE_ALREADY_EXISTS),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryExist = new EntityResultMapImpl();
                                    erQueryExist.put(RoleDao.ID_ROLENAME, List.of(roleId));
                                    erQueryExist.put(RoleDao.ROLENAME, List.of("roleName"));

                                    return when(daoHelper.query(Mockito.any(RoleDao.class), Mockito.anyMap(), anyList())).thenReturn(erQueryExist);
                                }
                        )
                )
                //endregion
        );
    }
}
