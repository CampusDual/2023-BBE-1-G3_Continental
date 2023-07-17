package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.Map;

public interface IParkingService {
    public EntityResult parkingEnter(Map<?,?> attrMap);
    public EntityResult parkingExit(Map<?,?> attrMap);
    public EntityResult parkingInsert(Map<String,Object> attrMap);
    public EntityResult parkingUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    public EntityResult parkingDelete(Map<String, Object> keyMap);
}
