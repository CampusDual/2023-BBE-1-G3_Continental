package com.hotel.continental.model.core.service;


import com.hotel.continental.api.core.service.IUserService;
import com.hotel.continental.model.core.dao.UserDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Lazy
@Service("UserService")
public class UserService implements IUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    public void loginQuery(Map<?, ?> key, List<?> attr) {
        throw new UnsupportedOperationException();
    }

    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(userDao, keyMap, attrList);
    }
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userInsert(Map<?, ?> attrMap) {
        return this.daoHelper.insert(userDao, attrMap);
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
