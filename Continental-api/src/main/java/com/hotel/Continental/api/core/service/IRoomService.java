package com.hotel.Continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRoomService {
    public EntityResult roomQuery(Map<?, ?> keyMap, List<?> attrList);
    public EntityResult roomInsert(Map<?, ?> attrMap);
    public EntityResult roomDelete(Map<?, ?> keyMap);
}
