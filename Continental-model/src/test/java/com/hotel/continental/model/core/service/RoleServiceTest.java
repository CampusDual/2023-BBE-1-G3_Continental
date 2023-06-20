package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.RoleDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
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
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestRoleInsert {
        @Test
        @DisplayName("Test role insert good")
        void testRoleInsertGood() {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL);
            EntityResult erQuery=new EntityResultMapImpl();
            erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);
            Map<String, Object> attr = new HashMap<>();
            attr.put(RoleDao.ROLENAME,"admin");
            //el insert comprueba a traves de un query que no existe asique devolvemos un entity result okey+vacio
            when(daoHelper.query(any(RoleDao.class), anyMap(), anyList())).thenReturn(erQuery);
            //Ejecutamos el insert
            when(daoHelper.insert(any(RoleDao.class), anyMap())).thenReturn(er);
            EntityResult result = roleService.roleInsert(attr);
            Assertions.assertEquals(EntityResult.OPERATION_SUCCESSFUL, result.getCode());
        }

        @Test
        @DisplayName("Test role insert null data ")
        void testRoleInsertNullData() {
            Map<String, Object> attr = new HashMap<>();
            //Ejecutamos el insert, al no tener datos nos va petar sin llegar a tener que mockear nada
            EntityResult result = roleService.roleInsert(attr);
            Assertions.assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            Assertions.assertEquals(ErrorMessages.NECESSARY_DATA, result.getMessage());
        }
        @Test
        @DisplayName("Test role insert empty data")
        void testRoleInsertEmptyData() {
            Map<String, Object> attr = new HashMap<>();
            attr.put(RoleDao.ROLENAME,"");
            //Ejecutamos el insert, al no tener datos nos va petar sin llegar a tener que mockear nada
            EntityResult result = roleService.roleInsert(attr);
            Assertions.assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            Assertions.assertEquals(ErrorMessages.NECESSARY_DATA, result.getMessage());
        }
        @Test
        @DisplayName("Test role insert duplicated data")
        void testRoleInsertDuplicatedData() {
            EntityResult erQuery=new EntityResultMapImpl();
            erQuery.put(RoleDao.ROLENAME, List.of("admin"));
            erQuery.put(RoleDao.ID_ROLENAME, List.of(1));
            erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);
            Map<String, Object> attr = new HashMap<>();
            attr.put(RoleDao.ROLENAME,"admin");
            //el insert comprueba a traves de un query que no existe asique devolvemos un entity result okey+vacio
            when(daoHelper.query(any(RoleDao.class), anyMap(), anyList())).thenReturn(erQuery);
            //no hace falta mockear el insert porque no llega a el,dado qeu el error salta antes
            EntityResult result = roleService.roleInsert(attr);
            Assertions.assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }
    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestRoleUpdate {
        @Test
        @DisplayName("Test role update good")
        void testRoleUpdateGood() {
            //Query resultado
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_SUCCESSFUL);
            //Query buscar id, correcta+1 resultado
            EntityResult erQueryID=new EntityResultMapImpl();
            erQueryID.setCode(EntityResult.OPERATION_SUCCESSFUL);
            erQueryID.put(RoleDao.ID_ROLENAME, List.of(1));
            //Query buscar rolename, correcta+ 0 resultado
            EntityResult erQueryRolename=new EntityResultMapImpl();
            erQueryRolename.setCode(EntityResult.OPERATION_SUCCESSFUL);
            Map<String, Object> attr = new HashMap<>();
            attr.put(RoleDao.ROLENAME,"admin");
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoleDao.ID_ROLENAME,1);
            //el insert comprueba a traves de un query que existe asique devolvemos un entity result okey+1 resultado
            when(daoHelper.query(any(RoleDao.class), anyMap(), anyList())).thenReturn(erQueryID).thenReturn(erQueryRolename);
            //Ejecutamos el insert
            when(daoHelper.update(any(RoleDao.class), anyMap(),anyMap())).thenReturn(er);
            EntityResult result = roleService.roleUpdate(attr,keyMap);
            System.out.println(result.getMessage());
            Assertions.assertEquals(EntityResult.OPERATION_SUCCESSFUL, result.getCode());
        }

        @Test
        @DisplayName("Test role update null key ")
        void testRoleUpdateNullKey() {
            Map<String, Object> attr = new HashMap<>();
            Map<String, Object> keyMap = new HashMap<>();
            //Ejecutamos el update, al no tener key nos va petar sin llegar a tener que mockear nada
            EntityResult result = roleService.roleUpdate(attr,keyMap);
            Assertions.assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            Assertions.assertEquals(ErrorMessages.NECESSARY_KEY, result.getMessage());
        }
        @Test
        @DisplayName("Test role update null key")
        void testRoleUpdateNullData() {
            Map<String, Object> attr = new HashMap<>();
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoleDao.ID_ROLENAME,1);
            //Ejecutamos el update, al no tener datos nos va petar sin llegar a tener que mockear nada
            EntityResult result = roleService.roleUpdate(attr,keyMap);
            Assertions.assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            Assertions.assertEquals(ErrorMessages.NECESSARY_DATA, result.getMessage());
        }
        @Test
        @DisplayName("Test role update empty data")
        void testRoleUpdateEmptyData() {
            Map<String, Object> attr = new HashMap<>();
            attr.put(RoleDao.ROLENAME,"");
            Map<String, Object> keyMap = new HashMap<>();
            keyMap.put(RoleDao.ID_ROLENAME,1);
            //Ejecutamos el insert, al no tener datos nos va petar sin llegar a tener que mockear nada
            EntityResult result = roleService.roleInsert(attr);
            Assertions.assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
            Assertions.assertEquals(ErrorMessages.NECESSARY_DATA, result.getMessage());
        }
        @Test
        @DisplayName("Test role update duplicated data")
        void testRoleUpdateDuplicatedData() {
            EntityResult erQuery=new EntityResultMapImpl();
            erQuery.put(RoleDao.ROLENAME, List.of("admin"));
            erQuery.put(RoleDao.ID_ROLENAME, List.of(1));
            erQuery.setCode(EntityResult.OPERATION_SUCCESSFUL);
            Map<String, Object> attr = new HashMap<>();
            attr.put(RoleDao.ROLENAME,"admin");
            //el insert comprueba a traves de un query que no existe asique devolvemos un entity result okey+vacio
            when(daoHelper.query(any(RoleDao.class), anyMap(), anyList())).thenReturn(erQuery);
            //no hace falta mockear el insert porque no llega a el,dado qeu el error salta antes
            EntityResult result = roleService.roleInsert(attr);
            Assertions.assertEquals(EntityResult.OPERATION_WRONG, result.getCode());
        }
    }
}
