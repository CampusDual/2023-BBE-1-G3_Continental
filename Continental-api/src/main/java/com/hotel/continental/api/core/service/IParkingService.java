package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.Map;

public interface IParkingService {
    public EntityResult parkingEnter(Map<?,?> attrMap);
    public EntityResult parkingExit(Map<?,?> attrMap);
}
