package com.hotel.Continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface IHotelService {
    public EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList);
    public EntityResult hotelInsert(Map<?, ?> attrMap);
    public EntityResult hotelDelete(Map<?, ?> keyMap) throws ParseException;
    public EntityResult hotelUpdate(Map<String, Object> attrMap, Map<?,?> keyMap);
}
