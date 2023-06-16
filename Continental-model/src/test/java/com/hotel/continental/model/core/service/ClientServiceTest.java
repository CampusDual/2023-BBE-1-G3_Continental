package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.ClientDao;
import com.hotel.continental.model.core.service.ClientService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        @Test
        @DisplayName("Test client insert with empty data")
        void testClientInsertEmptyData() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String,Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, "");
            clientToInsert.put(ClientDao.NAME, "");
            clientToInsert.put(ClientDao.COUNTRYCODE, "");
            EntityResult result = clientService.clientInsert(clientToInsert);
            Assertions.assertEquals(1, result.getCode());
        }
        @Test
        @DisplayName("Test client insert with null data")
        void testClientInsertNullData() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String,Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, null);
            clientToInsert.put(ClientDao.NAME, null);
            clientToInsert.put(ClientDao.COUNTRYCODE, null);
            EntityResult result = clientService.clientInsert(clientToInsert);
            Assertions.assertEquals(1, result.getCode());
        }
        @Test
        @DisplayName("Test client insert with bad document")
        void testClientInsertBadDocument() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

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
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

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

        @Test
        @DisplayName("Test client update bad client")
        void testClientUpdateNullData() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String, Object> clientToUpdate = new HashMap<>();
            clientToUpdate.put(ClientDao.DOCUMENT, null);
            clientToUpdate.put(ClientDao.NAME, null);
            clientToUpdate.put(ClientDao.COUNTRYCODE, null);
            Map<String, Object> filter = new HashMap<>();
            filter.put(ClientDao.CLIENTID, "9");
            when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = clientService.clientUpdate(clientToUpdate, filter);
            Assertions.assertEquals(1, result.getCode());
        }

        @Test
        @DisplayName("Test client update with bad document")
        void testClientUpdateBadDocument() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String, Object> clientToUpdate = new HashMap<>();
            clientToUpdate.put(ClientDao.DOCUMENT, "12345678");
            clientToUpdate.put(ClientDao.NAME, "Tomás");
            clientToUpdate.put(ClientDao.COUNTRYCODE, "ES");

            Map<String, Object> filter = new HashMap<>();
            filter.put(ClientDao.CLIENTID, "9");
            when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = clientService.clientUpdate(clientToUpdate, filter);
            Assertions.assertEquals(1, result.getCode());
        }

        @Test
        @DisplayName("Test client update with bad countrycode")
        void testClientUpdateBadCountryCode() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String, Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, "12345678Z");
            clientToInsert.put(ClientDao.NAME, "Tomás");
            clientToInsert.put(ClientDao.COUNTRYCODE, "IS");

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
    public class ClientServiceQuery {
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

        @Test
        @DisplayName("Test bad query client")
        void testQueryWrong() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(ClientDao.CLIENTID, 9798);

            List<String> columns = new ArrayList<>();
            columns.add("CLIENTESIDS");

            when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = clientService.clientQuery(keyMap,columns);
            Assertions.assertEquals(1, result.getCode());
        }

        @Test
        @DisplayName("Test query with null data")
        void testQueryNullData() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(ClientDao.CLIENTID, null);

            List<String> columns = new ArrayList<>();
            columns.add(ClientDao.CLIENTID);

            when(daoHelper.query(any(ClientDao.class), anyMap(), anyList())).thenReturn(er);

            EntityResult result = clientService.clientQuery(keyMap, columns);
            Assertions.assertEquals(1, result.getCode());
        }
    }
}