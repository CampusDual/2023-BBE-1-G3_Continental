package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IBookingService {
    public EntityResult bookingQuery(Map<?,?> keyMap, List<?> attrList);
    public EntityResult bookingInsert(Map<String, Object> attrMap);
    public EntityResult bookingDelete(Map<?,?> keyMap);
    public  EntityResult bookingUpdate(Map<String, Object> attrMap, Map<?,?> keyMap);
    public EntityResult bookingCheckin(Map<String, Object> attrMap);
    public EntityResult bookingCheckout(Map<String, Object> attrMap);
    public EntityResult bookingPrice(Map<String, Object> attrMap);
}
