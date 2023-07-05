package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRoomTypeService;
import com.hotel.continental.model.core.dao.RoomTypeDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.dao.IOntimizeDaoHelper;
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
    public EntityResult roomtypeUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        //Compruebo que me envian la clave
        if (keyMap.get(RoomTypeDao.TYPEID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Compruebo que me envian los atributos a modificar
        if (attrMap.isEmpty()) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Compruebo que el tipo de habitacion existe
        Map<String, Object> attrMapRoomType = new HashMap<>();
        attrMapRoomType.put(RoomTypeDao.TYPEID, keyMap.get(RoomTypeDao.TYPEID));
        EntityResult erRoomType = this.daoHelper.query(this.roomtypeDao,attrMapRoomType,List.of(RoomTypeDao.TYPEID));
        if(erRoomType.calculateRecordNumber() == 0){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.ROOMTYPE_NOT_EXIST);
            return er;
        }
        return this.daoHelper.update(this.roomtypeDao, attrMap, keyMap);
    }
    @Override
    public EntityResult roomtypeInsert(Map<String,Object>attrMap) {
        if (attrMap.get(RoomTypeDao.TYPE) == null || attrMap.get(RoomTypeDao.PRICE) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        try {
        if (((Integer) attrMap.get(RoomTypeDao.PRICE)) > 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.PRICE_MINOR_0);
            return er;
        }
        } catch (NumberFormatException e) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.PRICE_NOT_NUMBER);
            return er;
        }
        
    }
}
