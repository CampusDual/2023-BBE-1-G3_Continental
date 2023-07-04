package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRefrigeratorsService;
import com.hotel.continental.model.core.dao.RefrigeratorsDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Lazy
@Service("RefrigeratorsService")
public class RefrigeratorsService implements IRefrigeratorsService {
    @Override
    public EntityResult refrigeratorsInsert(Map<?, ?> attrMap) {
        //Compruebo que me envia una capacidad y un room_id
        if (attrMap.get(RefrigeratorsDao.ROOM_ID) == null || attrMap.get(RefrigeratorsDao.CAPACITY) == null) {
            EntityResult erError = new EntityResultMapImpl();
            erError.setCode(EntityResult.OPERATION_WRONG);
            erError.setMessage(ErrorMessages.NECESSARY_DATA);
            return erError;
        }
        //Compruebo que la capacidad es un numero
        try {
            Integer.parseInt(attrMap.get(RefrigeratorsDao.CAPACITY).toString());
        } catch (NumberFormatException e) {
            EntityResult erError = new EntityResultMapImpl();
            erError.setCode(EntityResult.OPERATION_WRONG);
            erError.setMessage(ErrorMessages.CAPACITY_NOT_NUMBER);
            return erError;
        }
        //Compruebo que la capacidad es mayor que 0
        if (Integer.parseInt(attrMap.get(RefrigeratorsDao.CAPACITY).toString()) <= 0) {
            EntityResult erError = new EntityResultMapImpl();
            erError.setCode(EntityResult.OPERATION_WRONG);
            erError.setMessage(ErrorMessages.CAPACITY_NOT_POSITIVE);
            return erError;
        }
        
        return null;
    }
}
