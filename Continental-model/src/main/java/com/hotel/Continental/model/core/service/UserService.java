package com.hotel.continental.model.core.service;


import com.hotel.continental.api.core.service.IUserService;
import com.hotel.continental.model.core.dao.RoleDao;
import com.hotel.continental.model.core.dao.UserDao;
import com.hotel.continental.model.core.dao.UserRoleDao;
import com.hotel.continental.model.core.tools.Messages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;


@Lazy
@Service("UserService")
public class UserService implements IUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    public void loginQuery(Map<?, ?> key, List<?> attr) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(userDao, keyMap, attrList);
    }

    @Override
    public EntityResult userInsert(Map<?, ?> attrMap) {
        //Hay que asegurarse que el nif no este ya en la base de datos
        if (!attrMap.containsKey("role") || !attrMap.containsKey(UserDao.USER_) || !attrMap.containsKey(UserDao.PASSWORD) || !attrMap.containsKey(UserDao.NAME) ||
                !attrMap.containsKey(UserDao.SURNAME)|| !attrMap.containsKey(UserDao.NIF)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }

        String idRole = attrMap.remove("role").toString();
        String idUser = (String) attrMap.get(UserDao.USER_);

        Map<String, Object> filterUser = new HashMap<>();
        filterUser.put(UserDao.USER_, attrMap.get(UserDao.USER_));

        EntityResult query = this.daoHelper.query(this.userDao, filterUser, Arrays.asList(UserDao.USER_));

        if (query.calculateRecordNumber() > 0) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(1);
                er.setMessage(Messages.USER_ALREADY_EXIST);
                return er;
        }

        Map<String, Object> filterRole = new HashMap<>();
        filterRole.put(RoleDao.ID_ROLENAME, Integer.parseInt(idRole));

        EntityResult roles = this.daoHelper.query(this.roleDao, filterRole, Arrays.asList(RoleDao.ID_ROLENAME));

        if (roles.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.ROLE_DOESNT_EXIST);
            return er;
        }

        if (!attrMap.get(UserDao.PASSWORD).toString().matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.INCORRECT_PASSWORD);
            return er;
        }

        EntityResult user = this.daoHelper.insert(this.userDao, attrMap);

        Map<String, Object> attrRole = new HashMap<>();
        attrRole.put(UserRoleDao.ID_ROLENAME, idRole);
        attrRole.put(UserRoleDao.USER, idUser);
        //Insertamos el rol del usuario
        Map<String, Object> userRole = new HashMap<>();
        userRole.put(UserRoleDao.USER, attrMap.get(UserDao.USER_));
        userRole.put(UserRoleDao.ID_ROLENAME, idRole);
        this.daoHelper.insert(this.userRoleDao, userRole);

        return user;
    }
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        if(attrMap == null || attrMap.isEmpty()){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }

        if(!keyMap.containsKey(UserDao.USER_)){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }

        EntityResult queryUser = this.daoHelper.query(this.userDao, keyMap, Arrays.asList(UserDao.USER_));
        if(queryUser.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.USER_DOESNT_EXIST);
            return er;
        }

        return this.daoHelper.update(this.userDao, attrMap, keyMap);
    }
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userDelete(Map<?, ?> keyMap) {
        if (!keyMap.containsKey(UserDao.USER_)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }

        EntityResult query = this.daoHelper.query(this.userDao, keyMap, Arrays.asList(UserDao.USER_));
        if (query.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.USER_DOESNT_EXIST);
            return er;
        }

        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put("user_down_date", new Timestamp(Calendar.getInstance().getTimeInMillis()));
        return this.daoHelper.update(this.userDao, attrMap, keyMap);
    }
}
