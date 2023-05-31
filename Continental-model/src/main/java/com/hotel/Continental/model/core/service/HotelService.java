package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IHotelService;
import com.hotel.Continental.api.core.service.IUserService;
import com.hotel.Continental.model.core.dao.BookDao;
import com.hotel.Continental.model.core.dao.HotelDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

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

    @Override
    public EntityResult hotelUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        return this.daoHelper.update(this.hotelDao, attrMap, keyMap);
    }

    public EntityResult hotelDelete(Map<?, ?> keyMap) throws ParseException {
        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put(HotelDao.HOTELDOWNDATE, new Timestamp(System.currentTimeMillis()));
        return this.daoHelper.update(this.hotelDao, attrMap, keyMap);
    }
}
