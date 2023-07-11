package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.RefrigeratorStockDao;
import com.hotel.continental.model.core.dao.RefrigeratorsDao;
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
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
    void testRegrigeratorInsert(String testCaseName, Map<String, Object> attrMap, Map<?, ?> keyMap, EntityResult expectedResult, List<Supplier> mock) {
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
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();
                                    erQueryProductDefault.put(ProductsDao.PRODUCTID, List.of(2));

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                },
                                (Supplier) () -> {
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
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryProductDefault = new EntityResultMapImpl();
                                    erQueryProductDefault.put(ProductsDao.PRODUCTID, List.of(2));

                                    return Mockito.when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProductDefault);
                                },
                                (Supplier) () -> {
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
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_DATA),
                        List.of()
                ),
                //endregion
                //region Test Case 4: Update RefrigeratorDafault product not exist
                Arguments.of(
                        "Update RefrigeratorDafault product not exist",
                        Map.of(RefrigeratorStockDao.STOCK, 2),
                        Map.of(RefrigeratorStockDao.PRODUCTID, 2),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.PRODUCT_NOT_EXIST),
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
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.STOCK_NOT_NUMBER),
                        List.of()
                ),
                Arguments.of(
                        "Update RefrigeratorDafault with bad stock(not positive)",
                        Map.of(RefrigeratorStockDao.STOCK, -1),
                        Map.of(RefrigeratorStockDao.PRODUCTID, 2),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.STOCK_NOT_POSITIVE),
                        List.of()
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
