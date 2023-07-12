package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRefrigeratorStockService {
    public EntityResult refrigeratorStockUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap);
    public EntityResult refrigeratorDefaultUpdate(Map<String,Object> attrMap, Map<?,?> keyMap);
    public EntityResult refrigeratorStockQuery(Map<String,Object> keyMap, List<String> attrList);

}
