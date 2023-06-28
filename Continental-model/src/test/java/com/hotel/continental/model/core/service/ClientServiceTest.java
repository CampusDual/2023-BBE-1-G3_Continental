package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.ClientDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
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
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ClientServiceTest {
    @Mock
    DefaultOntimizeDaoHelper daoHelper;

    @InjectMocks
    ClientService clientService;

    @Mock
    ClientDao clientDao;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ClienteServiceInsert{
        @Test
        @DisplayName("Test client insert")
        void testClientInsertGood() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String,Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, "12345678Z");
            clientToInsert.put(ClientDao.NAME, "Tomás");
            clientToInsert.put(ClientDao.COUNTRYCODE, "ES");

            when(daoHelper.query(any(ClientDao.class),anyMap(),anyList())).thenReturn(er);
            when(daoHelper.insert(any(ClientDao.class),anyMap())).thenReturn(er);

            EntityResult result = clientService.clientInsert(clientToInsert);
            Assertions.assertEquals(0, result.getCode());
        }

        @ParameterizedTest
        @ArgumentsSource(testInsertClientNullAndEmptyData.class)
        @DisplayName("Test client insert with null data and empty data")
        void testClientInsertNullData(HashMap<String, Object> clientToInsert) {
            EntityResult result = clientService.clientInsert(clientToInsert);
            Assertions.assertEquals(1, result.getCode());
            Assertions.assertEquals(ErrorMessages.NECESSARY_DATA, result.getMessage());
        }

        @Test
        @DisplayName("Test client insert with bad document")
        void testClientInsertBadDocument() {
            Map<String,Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, "12345678");
            clientToInsert.put(ClientDao.NAME, "Tomás");
            clientToInsert.put(ClientDao.COUNTRYCODE, "ES");

            EntityResult result = clientService.clientInsert(clientToInsert);
            Assertions.assertEquals(1, result.getCode());
        }
        @Test
        @DisplayName("Test client insert with bad CountryCode")
        void testClientInsertBadCountryCode(){
            Map<String,Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, "12345678Z");
            clientToInsert.put(ClientDao.NAME, "Tomás");
            clientToInsert.put(ClientDao.COUNTRYCODE, "ESPAÑA");

            EntityResult result = clientService.clientInsert(clientToInsert);
            Assertions.assertEquals(1, result.getCode());
        }
        @Test
        @DisplayName("Test client insert with duplicate document")
        void testClientInsertDuplicateDocument(){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("document", List.of("12345678Z"));

            Map<String,Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, "12345678Z");
            clientToInsert.put(ClientDao.NAME, "Tomás");
            clientToInsert.put(ClientDao.COUNTRYCODE, "ES");

            when(daoHelper.query(any(ClientDao.class),anyMap(),anyList())).thenReturn(er);

            EntityResult result = clientService.clientInsert(clientToInsert);
            Assertions.assertEquals(1, result.getCode());
        }
    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ClienteServiceUpdate{
        @Test
        @DisplayName("Test client update")
        void testCliendUpdateGood() {
            //Primer er para simular que el id existe en la base de datos
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(ClientDao.CLIENTID, List.of(1));
            //Segundo er simula que el documento no existe en la base de datos
            EntityResult er2 = new EntityResultMapImpl();
            er2.setCode(0);

            Map<String,Object> clientToUpdate = new HashMap<>();
            clientToUpdate.put(ClientDao.DOCUMENT, "12345678Z");
            clientToUpdate.put(ClientDao.NAME, "Tomás");
            clientToUpdate.put(ClientDao.COUNTRYCODE, "ES");

            Map<String,Object> clientToFilter = new HashMap<>();
            clientToFilter.put(ClientDao.CLIENTID, 1);

            when(daoHelper.query(any(ClientDao.class),anyMap(),anyList())).thenReturn(er,er2);
            when(daoHelper.update(any(ClientDao.class),anyMap(),anyMap())).thenReturn(er);

            EntityResult result = clientService.clientUpdate(clientToUpdate,clientToFilter);
            Assertions.assertEquals(0, result.getCode());
        }


        @ParameterizedTest
        @NullSource
        @DisplayName("Test client update bad client")
        void testClientUpdateNullData(String nullParameter) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String, Object> clientToUpdate = new HashMap<>();
            clientToUpdate.put(ClientDao.DOCUMENT, nullParameter);
            clientToUpdate.put(ClientDao.NAME, nullParameter);
            clientToUpdate.put(ClientDao.COUNTRYCODE, nullParameter);
            Map<String, Object> filter = new HashMap<>();
            filter.put(ClientDao.CLIENTID, "9");

            when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = clientService.clientUpdate(clientToUpdate, filter);
            Assertions.assertEquals(1, result.getCode());
        }

        @ParameterizedTest
        @ArgumentsSource(testInsertClientWithBadCountryCodeAndDocument.class)
        @DisplayName("Test client update with bad countrycode")
        void testClientUpdateBadCountryCodeAndDocument(HashMap<String, Object> clientToInsert) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String, Object> filter = new HashMap<>();
            filter.put(ClientDao.CLIENTID, "9");

            when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = clientService.clientUpdate(clientToInsert, filter);
            Assertions.assertEquals(1, result.getCode());
        }

        @Test
        @DisplayName("Test update client with a new document already existent")
        void testClientUpdateDuplicateDocument() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put("document", List.of("12345678Z"));

            Map<String, Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, "12345678Z");
            clientToInsert.put(ClientDao.NAME, "Tomás");
            clientToInsert.put(ClientDao.COUNTRYCODE, "ES");

            when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = clientService.clientInsert(clientToInsert);
            Assertions.assertEquals(1, result.getCode());
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ClienteServiceDelete {
        @Test
        @DisplayName("Test correct client delete")
        void testClientDeleteGood() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(ClientDao.CLIENTID, List.of(1));

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(ClientDao.CLIENTID, 7);

            when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(er);
            when(daoHelper.update(any(ClientDao.class), anyMap(), anyMap())).thenReturn(er);

            EntityResult result = clientService.clientDelete(keyMap);

            Assertions.assertEquals(0, result.getCode());
        }

        @Test
        @DisplayName("Test null client delete")
        void testClientDeleteNull() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(ClientDao.CLIENTID, null);

            when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = clientService.clientDelete(keyMap);

            Assertions.assertEquals(1, result.getCode());
        }
    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class ClientServiceQuery {
        @Test
        @DisplayName("Test good query client")
        void testQueryGood() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);
            er.put(ClientDao.CLIENTID, List.of(1));

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(ClientDao.CLIENTID, 8);

            List<String> columns = new ArrayList<>();
            columns.add(ClientDao.CLIENTID);

            when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = clientService.clientQuery(keyMap, columns);
            Assertions.assertEquals(0, result.getCode());
        }

        @ParameterizedTest
        @ArgumentsSource(testInsertWrongClientAndNullData.class)
        @DisplayName("Test query with null data and bad client")
        void testQueryNullDataAndWrongClient(HashMap<String, Object> keyMap, ArrayList<String> columns) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);

            EntityResult result = clientService.clientQuery(keyMap, columns);
            Assertions.assertEquals(1, result.getCode());
        }
    }

    public static class testInsertClientNullAndEmptyData implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(new HashMap<String, Object>() {{
                        put(ClientDao.DOCUMENT, null);
                        put(ClientDao.NAME, null);
                        put(ClientDao.COUNTRYCODE, null);
                    }}, new HashMap<String, Object>()).map(Arguments::of);
        }
    }

    public static class testInsertClientWithBadCountryCodeAndDocument implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(new HashMap<String, Object>() {{
                        put(ClientDao.DOCUMENT, "12345678Z");
                        put(ClientDao.NAME, "Tomás");
                        put(ClientDao.COUNTRYCODE, "IS");
                    }},
                    new HashMap<String, Object>(){{
                        put(ClientDao.DOCUMENT, "12345678Z");
                        put(ClientDao.NAME, "Tomás");
                        put(ClientDao.COUNTRYCODE, "ES");
                    }}).map(Arguments::of);
        }
    }

    public static class testInsertWrongClientAndNullData implements ArgumentsProvider {
        @Override
        public Stream<? extends Arguments> provideArguments(ExtensionContext context) {
            return Stream.of(Arguments.of(new HashMap<String, Object>() {{
                                 put(ClientDao.CLIENTID, null);
                             }},
                            new ArrayList<String>(){{
                                add(ClientDao.CLIENTID);
                            }}),
                    Arguments.of(new HashMap<String, Object>(){{
                            put(ClientDao.CLIENTID, 9798);
                    }},
                            new ArrayList<String>(){{
                                add("CLIENTESIDS");
                            }}));
        }
    }
}