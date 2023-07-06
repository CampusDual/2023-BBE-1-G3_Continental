package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRefrigeratorStockService {
    public EntityResult refrigeratorStockInsert(Map<?, ?> attrMap);
    public EntityResult refrigeratorStockQuery(Map<?,?> keyMap, List<?> attrList);
    public EntityResult refrigeratorStockDelete(Map<?,?>keyMap);
    public EntityResult refrigeratorStockMinusOne(Map<?,?> attrMap);
    public EntityResult refillStock(Map<?,?> attrMap);
    public EntityResult refrigeratorDefaultUpdate(Map<String,Object> attrMap);
}
