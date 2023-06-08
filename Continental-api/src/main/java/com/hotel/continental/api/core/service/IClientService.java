package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.Map;

public interface IClientService {
    public EntityResult clientInsert(Map<String, Object> attrMap);
    public EntityResult clientUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap);
    public EntityResult clientDelete(Map<?,?> keyMap);
    public EntityResult clientQuery(Map<String, Object> keyMap, List<?> attrList);
}
