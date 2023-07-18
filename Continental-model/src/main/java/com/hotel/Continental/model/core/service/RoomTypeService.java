package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRoomTypeService;
import com.hotel.continental.model.core.dao.RoomTypeDao;
import com.hotel.continental.model.core.tools.Messages;
import com.hotel.continental.model.core.tools.Validation;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("RoomTypeService")
public class RoomTypeService implements IRoomTypeService {
    @Autowired
    RoomTypeDao roomtypeDao;
    @Autowired
    DefaultOntimizeDaoHelper daoHelper;

    @Override
    public EntityResult roomtypeInsert(Map<?, ?> attrMap) {
        if(attrMap.get(RoomTypeDao.TYPE) == null || attrMap.get(RoomTypeDao.PRICE) == null){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }

        //Comprobar formato correcto en price
        if(attrMap.get(RoomTypeDao.PRICE) != null) {
            EntityResult checkNumber = Validation.checkNumber(attrMap.get(RoomTypeDao.PRICE).toString(), Messages.PRICE_NOT_POSITIVE, Messages.PRICE_NOT_NUMBER);
            if(checkNumber.getCode() == EntityResult.OPERATION_WRONG) {
                return checkNumber;
            }
        }

        return this.daoHelper.insert(this.roomtypeDao, attrMap);
    }

    @Override
    public EntityResult roomtypeUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        //Compruebo que me envian la clave
        if (keyMap.get(RoomTypeDao.TYPE_ID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }
        //Compruebo que me envian los atributos a modificar
        if (attrMap.isEmpty()) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }
        //Compruebo que el tipo de habitacion existe
        Map<String, Object> attrMapRoomType = new HashMap<>();
        attrMapRoomType.put(RoomTypeDao.TYPE_ID, keyMap.get(RoomTypeDao.TYPE_ID));
        EntityResult erRoomType = this.daoHelper.query(this.roomtypeDao,attrMapRoomType,List.of(RoomTypeDao.TYPE_ID));
        if(erRoomType.calculateRecordNumber() == 0){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.ROOMTYPE_NOT_EXIST);
            return er;
        }

        //Comprobar formato correcto en price
        if(attrMap.get(RoomTypeDao.PRICE) != null) {
            EntityResult checkNumber = Validation.checkNumber(attrMap.get(RoomTypeDao.PRICE).toString(), Messages.PRICE_NOT_POSITIVE, Messages.PRICE_NOT_NUMBER);
            if(checkNumber.getCode() == EntityResult.OPERATION_WRONG) {
                return checkNumber;
            }
        }

        return this.daoHelper.update(this.roomtypeDao, attrMap, keyMap);
    }
}
