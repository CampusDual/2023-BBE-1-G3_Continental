package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IAccessCardAssignmentService;
import com.hotel.continental.model.core.dao.AccessCardAssignmentDao;
import com.hotel.continental.model.core.dao.AccessCardDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import javax.swing.text.html.parser.Entity;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("AccessCardAssignmentService")
public class AccessCardAssignmentService implements IAccessCardAssignmentService {
    @Autowired
    private AccessCardAssignmentDao accessCardAssignmentDao;
    @Autowired
    private AccessCardDao accessCardDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Override
    public EntityResult accessCardAssignmentInsert(Map<String, Object> attrMap) {
        if (attrMap.get(AccessCardAssignmentDao.ACCESSCARDID) == null || attrMap.get(AccessCardAssignmentDao.BOOKINGID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        EntityResult query = this.daoHelper.query(this.accessCardAssignmentDao, attrMap, List.of(AccessCardAssignmentDao.ACCESSCARDASIGNMENT));
        if (query.calculateRecordNumber() > 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.ACCESS_CARD_ALREADY_GIVEN);
            return er;
        }
        return null;
    }

    @Override
    public EntityResult lostCard(Map<String, Object> attrMap) {
        //Compruebo que me da el id de la tarjeta
        if (attrMap.get(AccessCardAssignmentDao.ACCESSCARDID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }

        //Le cambiamos el estado a la tarjeta
        Map<String, Object> attrMapCard = new HashMap<>();
        attrMapCard.put(AccessCardDao.AVALIABLE, false);
        attrMapCard.put(AccessCardDao.HOTELID, null);
        attrMapCard.put(AccessCardDao.CARDDOWNDATE, new Timestamp(System.currentTimeMillis()));
        EntityResult update = this.daoHelper.update(this.accessCardDao, attrMapCard, attrMap);

        //Recogemos el id de la tabla accessCardAssigment
        EntityResult accessCardAssignment = this.daoHelper.query(this.accessCardAssignmentDao, attrMap, List.of(AccessCardAssignmentDao.ACCESSCARDASIGNMENT));

        //Comprobamos si no está vacia
        if (accessCardAssignment.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_RECOVERED);
            return er;
        }
        return update;
    }

    @Override
    public EntityResult accessCardAssignmentRecover(Map<String, Object> attrMap) {
        //Se comprueba si la tarjeta está perdida
        if(attrMap.get(AccessCardDao.CARDDOWNDATE) != null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.ACCESS_CARD_LOST);
            return er;
        }
        //Compruebo que me da el id de la tarjeta
        if (attrMap.get(AccessCardAssignmentDao.ACCESSCARDID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Comprobamos que esa tarjeta existe,y comprobamos que esta asignada
        EntityResult query = this.daoHelper.query(this.accessCardDao, attrMap, List.of(AccessCardDao.AVALIABLE));
        if (!(boolean)query.getRecordValues(0).get(AccessCardDao.AVALIABLE)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_GIVEN);
            return er;
        }
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
        return update;
    }
}