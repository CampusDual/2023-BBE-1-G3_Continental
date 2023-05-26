package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IHotelService;
import com.hotel.Continental.api.core.service.IRoomService;
import com.hotel.Continental.model.core.dao.HotelDao;
import com.hotel.Continental.model.core.dao.RoomDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("RoomService")
public class RoomService implements IRoomService {

    @Autowired
    private RoomDao roomDao;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    public EntityResult roomQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(roomDao, keyMap, attrList);
    }

    public EntityResult roomInsert(Map<?, ?> attrMap) {
        return this.daoHelper.insert(roomDao, attrMap);
    }

    public EntityResult roomDelete(Map<?, ?> keyMap) {
        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put("hotel_down_date", new Timestamp(Calendar.getInstance().getTimeInMillis()));
        return this.daoHelper.update(this.roomDao, attrMap, keyMap);
    }
}
