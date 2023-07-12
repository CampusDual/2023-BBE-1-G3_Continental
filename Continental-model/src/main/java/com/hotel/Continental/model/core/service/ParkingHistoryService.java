package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.ParkingHistoryDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Map;

@Lazy
@Service("ParkingHistoryService")
public class ParkingHistoryService {
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private ParkingHistoryDao parkingHistoryDao;

    /**
     * Inserto de un registro en la tabla parking_history con datos id_parking id_booking,entry_date es el dia que entra
     * @param attrMap
     * @return
     */
    public EntityResult parkingHistoryEnter(Map<String, Object> attrMap) {
        //Solo va ser usada desde el servicio de parking
        return this.daoHelper.insert(parkingHistoryDao, attrMap);
    }

}
