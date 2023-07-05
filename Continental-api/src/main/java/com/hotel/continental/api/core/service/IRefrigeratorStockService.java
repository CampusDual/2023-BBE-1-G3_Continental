package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRefrigeratorStockService {
    public EntityResult refrigeratorStockInsert(Map<?, ?> attrMap);
    public EntityResult refrigeratorStockQuery(Map<?,?> keyMap, List<?> attrList);
}
