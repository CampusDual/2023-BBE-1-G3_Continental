package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.ProductsDao;
import com.hotel.continental.model.core.dao.RefrigeratorStockDao;
import com.hotel.continental.model.core.dao.RefrigeratorsDao;
import com.hotel.continental.model.core.tools.Extras;
import com.hotel.continental.model.core.tools.Messages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;

@ExtendWith(MockitoExtension.class)
class RefrigeratorStockServiceTest {
    @Mock
    static
    DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    RefrigeratorStockService refrigeratorStockSrv;
    @Mock
    RefrigeratorStockDao refrigeratorStockDao;
    @Mock
    RefrigeratorsDao refrigeratorsDao;
    @Mock
    ProductsDao productsDao;

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("refrigeratorDefaultUpdate")
    void testRefrigeratorDefaultUpdate(String testCaseName, Map<String, Object> attrMap, Map<?, ?> keyMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = refrigeratorStockSrv.refrigeratorDefaultUpdate(attrMap, keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> refrigeratorDefaultUpdate() {
        return Stream.of(
                //region Test Case 1: update RefrigeratorDefault stock with correct data
                Arguments.of(
                        "update RefrigeratorDefault stock with correct data",
                        Map.of(RefrigeratorStockDao.STOCK, 5),
                        Map.of(RefrigeratorStockDao.PRODUCTID, 2),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();
                                    erQueryProductDefault.put(ProductsDao.PRODUCTID, List.of(2));

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                },
                                () -> {
                                    EntityResult erQueryFridgeDefault = new EntityResultMapImpl();
                                    erQueryFridgeDefault.put(RefrigeratorStockDao.STOCKID, List.of(2));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorStockDao.class), anyMap(), anyList())).thenReturn(erQueryFridgeDefault);
                                },
                                (Supplier) () -> {
                                    EntityResult erUpdateFridgeDefault = new EntityResultMapImpl();
                                    erUpdateFridgeDefault.put(RefrigeratorStockDao.STOCKID, List.of(2));
                                    erUpdateFridgeDefault.put(RefrigeratorStockDao.STOCK, List.of(5));
                                    erUpdateFridgeDefault.put(RefrigeratorStockDao.PRODUCTID, List.of(2));
                                    erUpdateFridgeDefault.put(RefrigeratorStockDao.REFRIGERATORID, List.of(-1));

                                    return Mockito.when(daoHelper.update(any(RefrigeratorStockDao.class), anyMap(), anyMap())).thenReturn(erUpdateFridgeDefault);
                                }
                        )
                ),
                //endregion
                //region Test Case 2: Insert Product in RefrigeratorDefault
                Arguments.of(
                        "Insert Product in RefrigeratorDefault",
                        Map.of(RefrigeratorStockDao.STOCK, 5),
                        Map.of(RefrigeratorStockDao.PRODUCTID, 2),
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();
                                    erQueryProductDefault.put(ProductsDao.PRODUCTID, List.of(2));

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                },
                                () -> {
                                    EntityResult erQueryFridgeDefault = new EntityResultMapImpl();

                                    return Mockito.when(daoHelper.query(any(RefrigeratorStockDao.class), anyMap(), anyList())).thenReturn(erQueryFridgeDefault);
                                },
                                (Supplier) () -> {
                                    EntityResult erInsertFridgeDefault = new EntityResultMapImpl();
                                    erInsertFridgeDefault.put(RefrigeratorStockDao.STOCKID, List.of(2));

                                    return Mockito.when(daoHelper.insert(any(RefrigeratorStockDao.class), anyMap())).thenReturn(erInsertFridgeDefault);
                                }
                        )
                ),
                //endregion
                //region Test Case 3: Update RefrigeratorDafault with null data
                Arguments.of(
                        "Update RefrigeratorDefault with null data",
                        Map.of(),
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //endregion
                //region Test Case 4: Update RefrigeratorDafault product not exist
                Arguments.of(
                        "Update RefrigeratorDafault product not exist",
                        Map.of(RefrigeratorStockDao.STOCK, 2),
                        Map.of(RefrigeratorStockDao.PRODUCTID, 2),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.PRODUCT_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                }
                        )
                ),
                //endregion
                //region Test Case 5: Update RefrigeratorDafault with bad stock(not number)
                Arguments.of(
                        "Update RefrigeratorDafault with bad stock(not number)",
                        Map.of(RefrigeratorStockDao.STOCK, "a"),
                        Map.of(RefrigeratorStockDao.PRODUCTID, 2),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.STOCK_NOT_NUMBER),
                        List.of()
                ),
                Arguments.of(
                        "Update RefrigeratorDafault with bad stock(not positive)",
                        Map.of(RefrigeratorStockDao.STOCK, -1),
                        Map.of(RefrigeratorStockDao.PRODUCTID, 2),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.STOCK_NOT_POSITIVE),
                        List.of()
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("refrigeratorStockUpdate")
    void testRefrigeratorStockUpdate(String testCaseName, Map<String, Object> keyMap, Map<String, Object> attrMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = refrigeratorStockSrv.refrigeratorStockUpdate(attrMap, keyMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> refrigeratorStockUpdate() {
        Map<String,Object> attrMap = new HashMap<>();
        attrMap.putAll(Map.of(RefrigeratorStockDao.STOCK, 2));
        return Stream.of(
                //region Test Case 1: update RefrigeratorDefault stock with correct data
                Arguments.of(
                        "update RefrigeratorDefault stock with correct data",
                        Map.of(RefrigeratorStockDao.REFRIGERATORID, 2, RefrigeratorStockDao.PRODUCTID, 2),
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();
                                    erQueryProductDefault.put(ProductsDao.PRODUCTID, List.of(2));
                                    erQueryProductDefault.put(ProductsDao.NAME, List.of("name"));
                                    erQueryProductDefault.put(ProductsDao.PRICE, List.of(12));

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                },
                                () -> {
                                    EntityResult erQueryFridgeStock = new EntityResultMapImpl();
                                    erQueryFridgeStock.put(RefrigeratorStockDao.STOCKID, List.of(1, 2));
                                    erQueryFridgeStock.put(RefrigeratorStockDao.REFRIGERATORID, List.of(1));
                                    erQueryFridgeStock.put(RefrigeratorStockDao.STOCK, List.of(2, 2));

                                    EntityResult erQueryFridgeStockDefault = new EntityResultMapImpl();
                                    erQueryFridgeStockDefault.put(RefrigeratorStockDao.STOCKID, List.of(1, 2));
                                    erQueryFridgeStockDefault.put(RefrigeratorStockDao.REFRIGERATORID, List.of(-1));
                                    erQueryFridgeStockDefault.put(RefrigeratorStockDao.STOCK, List.of(5, 5));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorStockDao.class), anyMap(), anyList())).thenReturn(erQueryFridgeStock,erQueryFridgeStockDefault,erQueryFridgeStock);
                                },
                                () -> {

                                    EntityResult erQueryFridge = new EntityResultMapImpl();
                                    erQueryFridge.put(RefrigeratorStockDao.REFRIGERATORID, List.of(-1));
                                    erQueryFridge.put(RefrigeratorStockDao.STOCKID, List.of(2));
                                    erQueryFridge.put(RefrigeratorStockDao.PRODUCTID, List.of(2));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorsDao.class), anyMap(), anyList()))
                                            .thenReturn(erQueryFridge);
                                },
                                (Supplier) () -> {
                                    EntityResult erUpdateFridgeDefault = new EntityResultMapImpl();
                                    erUpdateFridgeDefault.put(RefrigeratorStockDao.STOCKID, List.of(2));
                                    erUpdateFridgeDefault.put(RefrigeratorStockDao.STOCK, List.of(5));
                                    erUpdateFridgeDefault.put(RefrigeratorStockDao.PRODUCTID, List.of(2));
                                    erUpdateFridgeDefault.put(RefrigeratorStockDao.REFRIGERATORID, List.of(-1));

                                    return Mockito.when(daoHelper.update(any(RefrigeratorStockDao.class), anyMap(), anyMap())).thenReturn(erUpdateFridgeDefault);
                                }
                        )
                ),
                //region Test Case 2: update RefrigeratorDefault stock with null key
                Arguments.of(
                        "update RefrigeratorDefault stock with null key",
                        Map.of(),
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_KEY),
                        List.of()
                ),
                //region Test Case 3: update RefrigeratorDefault stock with null data
                Arguments.of(
                        "update RefrigeratorDefault stock with null data",
                        Map.of(RefrigeratorStockDao.REFRIGERATORID, 2, RefrigeratorStockDao.PRODUCTID, 2),
                        Map.of(),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NECESSARY_DATA),
                        List.of()
                ),
                //region Test Case 4: update RefrigeratorDefault stock refrigerator blocked
                Arguments.of(
                        "update RefrigeratorDefault stock refrigerator blocked",
                        Map.of(RefrigeratorStockDao.REFRIGERATORID, -1, RefrigeratorStockDao.PRODUCTID, 2),
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.REFRIGERATOR_BLOCKED),
                        List.of()
                ),
                //region Test Case 5: update RefrigeratorDefault stock fridge not exist
                Arguments.of(
                        "update RefrigeratorDefault stock fridge not exist",
                        Map.of(RefrigeratorStockDao.REFRIGERATORID, 2, RefrigeratorStockDao.PRODUCTID, 2),
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.REFRIGERATOR_NOT_EXIST),
                        List.of(
                                (Supplier) () -> {

                                    EntityResult erQueryFridge = new EntityResultMapImpl();

                                    return Mockito.when(daoHelper.query(any(RefrigeratorsDao.class), anyMap(), anyList()))
                                            .thenReturn(erQueryFridge);
                                }
                        )
                ),
                //region Test Case 6: update RefrigeratorDefault stock fridge not exist
                Arguments.of(
                        "update RefrigeratorDefault stock fridge not exist",
                        Map.of(RefrigeratorStockDao.REFRIGERATORID, 2, RefrigeratorStockDao.PRODUCTID, 2),
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.PRODUCT_NOT_EXIST),
                        List.of(
                                () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                },
                                (Supplier) () -> {
                                    EntityResult erQueryFridge = new EntityResultMapImpl();
                                    erQueryFridge.put(RefrigeratorsDao.FRIDGE_ID, List.of(1));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorsDao.class), anyMap(), anyList()))
                                            .thenReturn(erQueryFridge);
                                }
                        )
                ),
                //region Test Case 7: update RefrigeratorDefault stock product not necessary
                Arguments.of(
                        "update RefrigeratorDefault stock product not necessary",
                        Map.of(RefrigeratorStockDao.REFRIGERATORID, 2, RefrigeratorStockDao.PRODUCTID, 2),
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.PRODUCT_NOT_NECESSARY),
                        List.of(
                                () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();
                                    erQueryProductDefault.put(ProductsDao.PRODUCTID, List.of(2));
                                    erQueryProductDefault.put(ProductsDao.NAME, List.of("name"));
                                    erQueryProductDefault.put(ProductsDao.PRICE, List.of(12));

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                },
                                () -> {
                                    EntityResult erQueryStockFridge = new EntityResultMapImpl();

                                    return Mockito.when(daoHelper.query(any(RefrigeratorStockDao.class), anyMap(), anyList())).thenReturn(erQueryStockFridge);
                                },
                                (Supplier) () -> {
                                    EntityResult erQueryFridge = new EntityResultMapImpl();
                                    erQueryFridge.put(RefrigeratorsDao.FRIDGE_ID, List.of(1));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorsDao.class), anyMap(), anyList()))
                                            .thenReturn(erQueryFridge);
                                }
                        )
                ),
                //region Test Case 8: update RefrigeratorDefault stock zero
                Arguments.of(
                        "update RefrigeratorDefault stock zero",
                        Map.of(RefrigeratorStockDao.REFRIGERATORID, 2, RefrigeratorStockDao.PRODUCTID, 2),
                        Map.of(RefrigeratorStockDao.STOCK, 0),
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.UPDATE_STOCK_ZERO),
                        List.of(
                                () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();
                                    erQueryProductDefault.put(ProductsDao.PRODUCTID, List.of(2));
                                    erQueryProductDefault.put(ProductsDao.NAME, List.of("name"));
                                    erQueryProductDefault.put(ProductsDao.PRICE, List.of(12));

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                },
                                () -> {
                                    EntityResult erQueryStockFridge = new EntityResultMapImpl();
                                    erQueryStockFridge.put(RefrigeratorStockDao.REFRIGERATORID, List.of(2));
                                    erQueryStockFridge.put(RefrigeratorStockDao.STOCKID, List.of(2));
                                    erQueryStockFridge.put(RefrigeratorStockDao.STOCK, List.of(2));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorStockDao.class), anyMap(), anyList())).thenReturn(erQueryStockFridge);
                                },
                                (Supplier) () -> {
                                    EntityResult erQueryFridge = new EntityResultMapImpl();
                                    erQueryFridge.put(RefrigeratorsDao.FRIDGE_ID, List.of(1));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorsDao.class), anyMap(), anyList()))
                                            .thenReturn(erQueryFridge);
                                }
                        )
                ),
                //region Test Case 9: update RefrigeratorDefault new stock higher than default
                Arguments.of(
                        "update RefrigeratorDefault new stock higher than default",
                        Map.of(RefrigeratorStockDao.REFRIGERATORID, 2, RefrigeratorStockDao.PRODUCTID, 2),
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NEW_STOCK_HIGHER_THAN_DEFAULT),
                        List.of(
                                () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();
                                    erQueryProductDefault.put(ProductsDao.PRODUCTID, List.of(2));
                                    erQueryProductDefault.put(ProductsDao.NAME, List.of("name"));
                                    erQueryProductDefault.put(ProductsDao.PRICE, List.of(12));

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                },
                                () -> {
                                    EntityResult erQueryStockFridge = new EntityResultMapImpl();
                                    erQueryStockFridge.put(RefrigeratorStockDao.REFRIGERATORID, List.of(2));
                                    erQueryStockFridge.put(RefrigeratorStockDao.STOCKID, List.of(2));
                                    erQueryStockFridge.put(RefrigeratorStockDao.STOCK, List.of(2));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorStockDao.class), anyMap(), anyList())).thenReturn(erQueryStockFridge);
                                },
                                (Supplier) () -> {
                                    EntityResult erQueryFridge = new EntityResultMapImpl();
                                    erQueryFridge.put(RefrigeratorsDao.FRIDGE_ID, List.of(1));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorsDao.class), anyMap(), anyList()))
                                            .thenReturn(erQueryFridge);
                                }
                        )
                ),
                //region Test Case 9: update RefrigeratorDefault new stock higher than default
                Arguments.of(
                        "update RefrigeratorDefault new stock higher than default",
                        Map.of(RefrigeratorStockDao.REFRIGERATORID, 2, RefrigeratorStockDao.PRODUCTID, 2),
                        attrMap,
                        Extras.createEntityResult(EntityResult.OPERATION_WRONG, Messages.NEW_STOCK_UNDER_ZERO),
                        List.of(
                                () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();
                                    erQueryProductDefault.put(ProductsDao.PRODUCTID, List.of(2));
                                    erQueryProductDefault.put(ProductsDao.NAME, List.of("name"));
                                    erQueryProductDefault.put(ProductsDao.PRICE, List.of(12));

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                },
                                () -> {
                                    EntityResult erQueryFridgeStock = new EntityResultMapImpl();
                                    erQueryFridgeStock.put(RefrigeratorStockDao.STOCKID, List.of(1, 2));
                                    erQueryFridgeStock.put(RefrigeratorStockDao.REFRIGERATORID, List.of(1));
                                    erQueryFridgeStock.put(RefrigeratorStockDao.STOCK, List.of(-5, -5));

                                    EntityResult erQueryFridgeStockDefault = new EntityResultMapImpl();
                                    erQueryFridgeStockDefault.put(RefrigeratorStockDao.STOCKID, List.of(1, 2));
                                    erQueryFridgeStockDefault.put(RefrigeratorStockDao.REFRIGERATORID, List.of(-1));
                                    erQueryFridgeStockDefault.put(RefrigeratorStockDao.STOCK, List.of(5, 5));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorStockDao.class), anyMap(), anyList())).thenReturn(erQueryFridgeStock,erQueryFridgeStockDefault,erQueryFridgeStock);
                                },
                                (Supplier) () -> {
                                    EntityResult erQueryFridge = new EntityResultMapImpl();
                                    erQueryFridge.put(RefrigeratorsDao.FRIDGE_ID, List.of(1));

                                    return Mockito.when(daoHelper.query(any(RefrigeratorsDao.class), anyMap(), anyList()))
                                            .thenReturn(erQueryFridge);
                                }
                        )
                )
                //endregion
        );
    }
}
