package com.hotel.Continental.model.core.service;

import com.hotel.Continental.model.core.dao.ClientDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
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
    public class ClientServiceInsert {
        @Test
        @DisplayName("Test client insert")
        void testClientInsert() {
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
    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class ClientServiceInsertWithEmptyData {
        @Test
        @DisplayName("Test client insert with empty data")
        void testClientInsert() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String,Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, "");
            clientToInsert.put(ClientDao.NAME, "");
            clientToInsert.put(ClientDao.COUNTRYCODE, "");
            when(daoHelper.query(any(ClientDao.class),anyMap(),anyList())).thenReturn(er);
            when(daoHelper.insert(any(ClientDao.class),anyMap())).thenReturn(er);
            EntityResult result = clientService.clientInsert(clientToInsert);
            Assertions.assertEquals(1, result.getCode());
        }
    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    public class ClientServiceInsertWithNullData {
        @Test
        @DisplayName("Test client insert with null data")
        void testClientInsert() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String,Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, null);
            clientToInsert.put(ClientDao.NAME, null);
            clientToInsert.put(ClientDao.COUNTRYCODE, null);
            when(daoHelper.query(any(ClientDao.class),anyMap(),anyList())).thenReturn(er);
            when(daoHelper.insert(any(ClientDao.class),anyMap())).thenReturn(er);
            EntityResult result = clientService.clientInsert(clientToInsert);
            Assertions.assertEquals(1, result.getCode());
        }
    }
}
