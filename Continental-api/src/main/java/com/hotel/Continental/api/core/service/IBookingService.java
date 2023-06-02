package com.hotel.Continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IBookingService {
    public EntityResult bookQuery(Map<?,?> keyMap, List<?> attrList);
    public EntityResult bookInsert(Map<String, Object> attrMap);
    public EntityResult bookDelete(Map<?,?> keyMap);
    public  EntityResult bookUpdate(Map<String, Object> attrMap, Map<?,?> keyMap);
}
