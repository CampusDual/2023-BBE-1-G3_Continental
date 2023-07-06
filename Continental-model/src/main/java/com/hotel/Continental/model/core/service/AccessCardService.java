package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IAccessCardService;
import com.hotel.continental.model.core.dao.AccessCardDao;
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
@Service("AccessCardService")
public class AccessCardService implements IAccessCardService {
    @Autowired
    private AccessCardDao accessCardDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult accesscardQuery(Map<?, ?> keyMap, List<?> attrList) {
        if(attrList.isEmpty()){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        EntityResult result = this.daoHelper.query(this.accessCardDao, keyMap, attrList);
        if (result == null || result.calculateRecordNumber() == 0) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_EXIST);
            return er;
        }
        return result;
    }
}
