package com.hotel.Continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface IClientService {
    public EntityResult clientQuery(Map<String, Object> keyMap, List<?> attrList);
    public EntityResult clientInsert(Map<String, Object> attrMap);
    public EntityResult clientDelete(Map<String, Object> keyMap);
    public EntityResult clienteUpdate(Map<String, Object> attrMap, Map<?,?> keyMap);
}
