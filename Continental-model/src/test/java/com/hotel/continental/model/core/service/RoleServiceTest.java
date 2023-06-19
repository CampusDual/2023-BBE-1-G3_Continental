package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.RoleDao;
import com.hotel.continental.model.core.dao.RoomDao;
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

public class RoleServiceTest {
    //Generame los test para el metodo roleQuery
    @Mock
    DefaultOntimizeDaoHelper daoHelper;
    @InjectMocks
    RoleService roleService;
    @Mock
    RoleDao roleDao;
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestRoleQuery {
        @Test
        @DisplayName("Test role query good")
        void testRoleQueryGood() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL);
            er.put(RoleDao.ROLENAME, List.of("admin"));

            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoleDao.ROLENAME,"admin");
            List<Object> attr = new ArrayList<>();
            attr.add(RoleDao.ID_ROLENAME);
            attr.add(RoleDao.ROLENAME);
            when(daoHelper.query(any(RoleDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roleService.roleQuery(keyMap, attr);
            Assertions.assertEquals(EntityResult.OPERATION_SUCCESSFUL, result.getCode());
        }

        @Test
        @DisplayName("Test role query good without filter ")
        void testRoleQueryGoodWitoutFilter() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL);
            er.put(RoleDao.ID_ROLENAME, List.of(1));
            er.put(RoleDao.ROLENAME, List.of("admin"));

            Map<String, Object> keyMap = new HashMap<>();
            List<Object> attr = new ArrayList<>();
            attr.add(RoleDao.ID_ROLENAME);
            attr.add(RoleDao.ROLENAME);

            when(daoHelper.query(any(RoleDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roleService.roleQuery(keyMap, attr);
            Assertions.assertEquals(EntityResult.OPERATION_SUCCESSFUL, result.getCode());
        }
        @Test
        @DisplayName("Test role query bad without columns ")
        void testRoleQueryBadWithoutColumns() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoleDao.ROLENAME,"admin");
            List<Object> attr = new ArrayList<>();
            when(daoHelper.query(any(RoleDao.class), anyMap(), anyList())).thenReturn(er);
            EntityResult result = roleService.roleQuery(keyMap, attr);
            Assertions.assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }
}
