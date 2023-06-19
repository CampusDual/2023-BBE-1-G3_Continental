package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRoleService;
import com.hotel.continental.model.core.dao.HotelDao;
import com.hotel.continental.model.core.dao.RoleDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
@Lazy
@Service("RoleService")
public class RoleService implements IRoleService {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roleQuery(Map<?, ?> keyMap, List<?> attrList) {
        EntityResult role = this.daoHelper.query(this.roleDao, keyMap, attrList);
        if(role == null || role.calculateRecordNumber() == 0){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ROLE_DOESNT_EXIST);
            return er;
        }
        return role;
    }
}
