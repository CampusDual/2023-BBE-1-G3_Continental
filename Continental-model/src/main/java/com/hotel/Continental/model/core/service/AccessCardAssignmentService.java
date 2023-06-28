package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IAccessCardAssignment;
import com.hotel.continental.model.core.dao.AccessCardAssignmentDao;
import com.hotel.continental.model.core.dao.AccessCardDao;
import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.assertj.core.internal.bytebuddy.implementation.bind.annotation.AllArguments;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("AccessCardAssignmentService")
public class AccessCardAssignmentService implements IAccessCardAssignment {
    @Autowired
    private AccessCardAssignmentDao accessCardAssignmentDao;
    @Autowired
    private AccessCardDao accessCardDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Override
    public EntityResult accesscardassignmentInsert(Map<String, Object> attrMap) {
        if (attrMap.get(AccessCardAssignmentDao.ACCESSCARDID) == null || attrMap.get(AccessCardAssignmentDao.BOOKINGID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        EntityResult query = this.daoHelper.query(this.accessCardAssignmentDao, attrMap, List.of(AccessCardAssignmentDao.ACCESSCARDASIGNMENT));
        if (query.calculateRecordNumber()>0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.ACCESS_CARD_ALREADY_GIVEN);
            return er;
        }
        return null;
    }
    @Override
    public EntityResult accesscardassignmentRecover(Map<String, Object> attrMap) {
        //Faltaria el tener en cuenta darla por perdida
        //Compruebo que me da el id de la tarjeta
        if (attrMap.get(AccessCardAssignmentDao.ACCESSCARDID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Comprobamos que esa tarjeta existe,y comprobamos que esta asignada
        EntityResult query = this.daoHelper.query(this.accessCardDao, attrMap, List.of(AccessCardDao.AVALIABLE));
        if ((boolean)query.getRecordValues(0).get(AccessCardDao.AVALIABLE)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_GIVEN);
            return er;
        }
        //Comprobamos que esta asignada a esa reserva
        //Le cambiamos el estado a la tarjeta
        Map<String, Object> attrMap2 = new HashMap<>();
        attrMap2.put(AccessCardDao.AVALIABLE, true);
        EntityResult update = this.daoHelper.update(this.accessCardDao, attrMap2, attrMap);
        if (update.getCode() == EntityResult.OPERATION_WRONG) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_RECOVERED);
            return er;
        }
        update.setMessage(ErrorMessages.ACCESS_CARD_RECOVERED);
        return update;
    }
}
