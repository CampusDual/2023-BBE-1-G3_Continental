package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IHotelService;
import com.hotel.Continental.api.core.service.IUserService;
import com.hotel.Continental.model.core.dao.HotelDao;
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
@Service("HotelService")
public class HotelService implements IHotelService {

    @Autowired
    private HotelDao hotelDao;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    public EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(hotelDao, keyMap, attrList);
    }

    public EntityResult hotelInsert(Map<?, ?> attrMap) {
        return this.daoHelper.insert(hotelDao, attrMap);
    }

    public EntityResult hotelDelete(Map<?, ?> keyMap) {
        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put("hotel_down_date", new Timestamp(Calendar.getInstance().getTimeInMillis()));
        return this.daoHelper.update(this.hotelDao, attrMap, keyMap);
    }

}
