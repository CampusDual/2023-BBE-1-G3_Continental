package com.hotel.continental.model.core.service;


import com.hotel.continental.api.core.service.IUserService;
import com.hotel.continental.model.core.dao.UserDao;
import com.hotel.continental.model.core.dao.UserRoleDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
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
    private DefaultOntimizeDaoHelper daoHelper;

    public void loginQuery(Map<?, ?> key, List<?> attr) {
        throw new UnsupportedOperationException();
    }

    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(userDao, keyMap, attrList);
    }
    public EntityResult userInsert(Map<?, ?> attrMap) {
        //Hay que asegurarse que el nif no este ya en la base de datos
        if (!attrMap.containsKey("role") || !attrMap.containsKey(UserDao.USER_) || !attrMap.containsKey(UserDao.PASSWORD) || !attrMap.containsKey(UserDao.NAME) ||
                !attrMap.containsKey(UserDao.SURNAME)|| !attrMap.containsKey(UserDao.NIF)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }

        String idRole = attrMap.remove("role").toString();

        EntityResult userId = this.daoHelper.insert(this.userDao, attrMap);
        Map<String, Object> attrRole = new HashMap<>();
        attrRole.put(UserRoleDao.id_rolename, idRole);
        attrRole.put(UserRoleDao.USER_, userId);

        this.daoHelper.insert(userRoleDao, attrRole);

        return userId;
    }
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        return this.daoHelper.update(userDao, attrMap, keyMap);
    }
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userDelete(Map<?, ?> keyMap) {
        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put("user_down_date", new Timestamp(Calendar.getInstance().getTimeInMillis()));
        return this.daoHelper.update(this.userDao, attrMap, keyMap);
    }

}
