package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.ProductsDao;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ProductServiceTest {
    @Mock
    static
    DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    ProductService productService;
    @Mock
    ProductsDao productDao;
    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("productInsert")
    void testProductInsert(String testCaseName, Map<String, Object> attr, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = productService.productsInsert(attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> productInsert() {
        return Stream.of(
                //region Test Case 1: Insert Refrigerator with correct data
                Arguments.of(
                        "Insert product with correct data",
                        Map.of(ProductsDao.PRODUCTID, 1, ProductsDao.NAME, "cocacola", ProductsDao.PRICE, 20.0),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erInsertProduct = new EntityResultMapImpl();
                                    erInsertProduct.put(ProductsDao.PRODUCTID, List.of(1));
                                    erInsertProduct.put(ProductsDao.PRICE, List.of(20.0));
                                    return when(daoHelper.insert(any(ProductsDao.class), anyMap())).thenReturn(erInsertProduct);
                                }
                        )
                ),
                //endregion
                //region Test Case 2: Insert Refrigerator with null data
                Arguments.of(
                        "Insert product with null data",
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_DATA),
                        List.of()
                ),
                //endregion
                //region Test Case 3: Insert Refrigerator with fail data (not number)
                Arguments.of(
                        "Insert product with fail data (not number)",
                        Map.of(ProductsDao.PRODUCTID, 1, ProductsDao.NAME, "cocacola", ProductsDao.PRICE, "a"),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.PRICE_NOT_NUMBER),
                        List.of()
                ),
                //endregion
                //region Test Case 4: Insert Refrigerator with fail data (not positive)
                Arguments.of(
                        "Insert product with fail data (not positive)",
                        Map.of(ProductsDao.PRODUCTID, 1, ProductsDao.NAME, "cocacola", ProductsDao.PRICE, -100.0),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.PRICE_MINOR_0),
                        List.of()
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("productUpdate")
    void testProductUpdate(String testCaseName, Map<String, Object> attrMap, Map<String, Object> keyMap, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = productService.productsUpdate(keyMap, attrMap);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> productUpdate() {
        return Stream.of(
                //region Test Case 1: Insert Refrigerator with correct data
                Arguments.of(
                        "Update product with correct data",
                        Map.of(ProductsDao.PRODUCTID, 1),
                        Map.of(ProductsDao.NAME, "cocacola", ProductsDao.PRICE, 20.0),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryProduct = new EntityResultMapImpl();
                                    erQueryProduct.put(ProductsDao.PRODUCTID, List.of(1));
                                    erQueryProduct.put(ProductsDao.NAME, List.of("cocacola"));
                                    erQueryProduct.put(ProductsDao.PRICE, List.of(20.0));

                                    EntityResult erUpdateProduct = new EntityResultMapImpl();
                                    erQueryProduct.put(ProductsDao.NAME, List.of("nestea"));
                                    erUpdateProduct.put(ProductsDao.PRICE, List.of(30.0));
                                    return List.of(when(daoHelper.query(any(ProductsDao.class), anyMap(),anyList())).thenReturn(erQueryProduct),
                                            when(daoHelper.update(any(ProductsDao.class), anyMap(), anyMap())).thenReturn(erUpdateProduct));
                                }
                        )
                ),
                //endregion
                //region Test Case 2: Update Refrigerator with product not exist
                Arguments.of(
                        "Update product with product no exist",
                        Map.of(ProductsDao.PRODUCTID, 2),
                        Map.of(ProductsDao.NAME, "cocacola", ProductsDao.PRICE, 20.0),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryProduct = new EntityResultMapImpl();
                                    erQueryProduct.put(ProductsDao.PRODUCTID, List.of(1));
                                    erQueryProduct.put(ProductsDao.NAME, List.of("cocacola"));
                                    erQueryProduct.put(ProductsDao.PRICE, List.of(20.0));

                                    EntityResult erUpdateProduct = new EntityResultMapImpl();
                                    erQueryProduct.put(ProductsDao.NAME, List.of("nestea"));
                                    erUpdateProduct.put(ProductsDao.PRICE, List.of(30.0));
                                    return List.of(when(daoHelper.query(any(ProductsDao.class), anyMap(),anyList())).thenReturn(erQueryProduct),
                                            when(daoHelper.update(any(ProductsDao.class), anyMap(), anyMap())).thenReturn(erUpdateProduct));
                                }
                        )
                ),
                //endregion
                //region Test Case 3: Update Refrigerator with null data
                Arguments.of(
                        "Update product with null key",
                        Map.of(),
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_KEY),
                        List.of()
                ),
                //endregion
                //region Test Case 3: Update Refrigerator with null data
                Arguments.of(
                        "Update product with null data",
                        Map.of(ProductsDao.PRODUCTID, 2),
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_DATA),
                        List.of()
                ),
                //endregion
                //region Test Case 4: Update Refrigerator with fail data (not number)
                Arguments.of(
                        "Update product with fail data (not number)",
                        Map.of(ProductsDao.PRODUCTID, 1),
                        Map.of(ProductsDao.PRODUCTID, 1, ProductsDao.NAME, "cocacola", ProductsDao.PRICE, "a"),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.PRICE_NOT_NUMBER),
                        List.of()
                ),
                //endregion
                //region Test Case 5: Update Refrigerator with fail data (not positive)
                Arguments.of(
                        "Update product with fail data (not positive)",
                        Map.of(ProductsDao.PRODUCTID, 1),
                        Map.of(ProductsDao.NAME, "cocacola", ProductsDao.PRICE, -100.0),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.PRICE_MINOR_0),
                        List.of()
                )
                //endregion
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("productQuery")
    void testProductQuery(String testCaseName, Map<String, Object> keyMap, List<String> attrList, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = productService.productsQuery(keyMap, attrList);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> productQuery() {
        return Stream.of(
                //region Test Case 1: Query product with filter
                Arguments.of(
                        "Query product with filter",
                        Map.of(ProductsDao.PRODUCTID, 1),
                        List.of(ProductsDao.PRODUCTID, ProductsDao.NAME, ProductsDao.PRICE),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryProduct = new EntityResultMapImpl();
                                    erQueryProduct.put(ProductsDao.PRODUCTID, List.of(1));
                                    erQueryProduct.put(ProductsDao.NAME, List.of("cocacola"));
                                    erQueryProduct.put(ProductsDao.PRICE, List.of(20.0));

                                    return when(daoHelper.query(any(ProductsDao.class), anyMap(),anyList())).thenReturn(erQueryProduct);
                                }
                        )
                ),
                //endregion
                //region Test Case 2: Query product without filter
                Arguments.of(
                        "Query product without filter",
                        Map.of(),
                        List.of(ProductsDao.PRODUCTID, ProductsDao.NAME, ProductsDao.PRICE),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryProduct = new EntityResultMapImpl();
                                    erQueryProduct.put(ProductsDao.PRODUCTID, List.of(1));
                                    erQueryProduct.put(ProductsDao.NAME, List.of("cocacola"));
                                    erQueryProduct.put(ProductsDao.PRICE, List.of(20.0));

                                    return when(daoHelper.query(any(ProductsDao.class), anyMap(),anyList())).thenReturn(erQueryProduct);
                                }
                        )
                ),
                //endregion
                //region Test Case 3: Query with product not exist
                Arguments.of(
                        "Query with product not exist",
                        Map.of(ProductsDao.PRODUCTID, 2),
                        List.of(ProductsDao.NAME, "cocacola", ProductsDao.PRICE, 20.0),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.PRODUCT_NOT_EXISTS),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryProduct = new EntityResultMapImpl();
                                    erQueryProduct.setCode(EntityResult.OPERATION_WRONG);
                                    erQueryProduct.setMessage(ErrorMessages.PRODUCT_NOT_EXISTS);

                                    return when(daoHelper.query(any(ProductsDao.class), anyMap(),anyList())).thenReturn(erQueryProduct);
                                }
                        )
                )
        );
    }

    @ParameterizedTest(name = "Test case {index} : {0}")
    @MethodSource("productDelete")
    void testProductDelete(String testCaseName, Map<String, Object> attr, EntityResult expectedResult, List<Supplier> mock) {
        //For each test case, execute the mock,to make sure the mock is called
        mock.forEach(Supplier::get);
        EntityResult result = productService.productsDelete(attr);
        // Assert
        assertEquals(expectedResult.getMessage(), result.getMessage());
        assertEquals(expectedResult.getCode(), result.getCode());
    }

    private static Stream<Arguments> productDelete() {
        return Stream.of(
                //region Test Case 1: Delete Refrigerator with correct data
                Arguments.of(
                        "Delete product with correct data",
                        Map.of(ProductsDao.PRODUCTID, 1),
                        createEntityResult(EntityResult.OPERATION_SUCCESSFUL, ""),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erDeleteProduct = new EntityResultMapImpl();
                                    erDeleteProduct.put(ProductsDao.PRODUCTID, List.of(1));

                                    EntityResult erQueryProduct = new EntityResultMapImpl();
                                    erQueryProduct.put(ProductsDao.PRODUCTID, List.of(1));
                                    erQueryProduct.put(ProductsDao.NAME, List.of("cocacola"));
                                    erQueryProduct.put(ProductsDao.PRICE, List.of(20.0));

                                    return List.of(when(daoHelper.delete(any(ProductsDao.class), anyMap())).thenReturn(erDeleteProduct),
                                            when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProduct));
                                }
                        )
                ),
                //endregion
                //region Test Case 2: Delete Refrigerator with null data
                Arguments.of(
                        "Delete product with null data",
                        Map.of(),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.NECESSARY_KEY),
                        List.of()
                ),
                //endregion
                //region Test Case 3: Delete Refrigerator with fail data (not product)
                Arguments.of(
                        "Delete product with fail data (not product)",
                        Map.of(ProductsDao.PRODUCTID, 1),
                        createEntityResult(EntityResult.OPERATION_WRONG, ErrorMessages.PRODUCT_NOT_EXISTS),
                        List.of(
                                (Supplier) () -> {
                                    EntityResult erQueryProduct = new EntityResultMapImpl();
                                    erQueryProduct.setCode(EntityResult.OPERATION_WRONG);
                                    erQueryProduct.setMessage(ErrorMessages.PRODUCT_NOT_EXISTS);
                                    return when(daoHelper.query(any(ProductsDao.class), anyMap(), anyList())).thenReturn(erQueryProduct);
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
