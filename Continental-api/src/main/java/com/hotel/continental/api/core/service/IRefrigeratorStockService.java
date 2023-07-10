package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRefrigeratorStockService {
    public EntityResult refrigeratorDefaultUpdate(Map<String,Object> attrMap);
}
