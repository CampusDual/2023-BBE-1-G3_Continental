package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRoomTypeService;
import com.hotel.continental.model.core.dao.RoomTypeDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.IOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Lazy
@Service("RoomTypeService")
public class RoomTypeService implements IRoomTypeService {
    @Autowired
    RoomTypeDao roomtypeDao;
    @Autowired
    IOntimizeDaoHelper daoHelper;
    @Override
    public EntityResult roomtypeUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        if (keyMap.get(RoomTypeDao.TYPEID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        if (attrMap.isEmpty()) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        return this.daoHelper.update(this.roomtypeDao, attrMap, keyMap);
    }
}
