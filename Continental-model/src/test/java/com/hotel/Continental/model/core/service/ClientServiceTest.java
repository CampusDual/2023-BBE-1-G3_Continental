package com.hotel.Continental.model.core.service;

import com.hotel.Continental.model.core.dao.ClientDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.HashMap;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyMap;
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
        void testClientInsert() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(0);

            Map<String,Object> clientToInsert = new HashMap<>();
            clientToInsert.put(ClientDao.DOCUMENT, "12345678Z");
            clientToInsert.put(ClientDao.NAME, "Tomás");
            clientToInsert.put(ClientDao.COUNTRYCODE, "ES");

            when(daoHelper.insert(any(ClientDao.class),anyMap())).thenReturn(er);

            EntityResult result = clientService.clientInsert(clientToInsert);

            Assertions.assertEquals(0, result.getCode());
        }
    }
}
