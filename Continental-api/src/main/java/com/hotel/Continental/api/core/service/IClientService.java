package com.hotel.Continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.text.ParseException;
import java.util.List;
import java.util.Map;

public interface IClientService {
    public EntityResult clientInsert(Map<String, Object> attrMap);
    public EntityResult clientUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap);
    public EntityResult clientDelete(Map<?,?> keyMap);
}
