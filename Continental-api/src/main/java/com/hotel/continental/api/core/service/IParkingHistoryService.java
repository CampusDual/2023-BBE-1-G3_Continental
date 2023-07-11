package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IParkingHistoryService {
    public EntityResult parkingHistoryInsert(Map<String, Object> attrMap);
}
