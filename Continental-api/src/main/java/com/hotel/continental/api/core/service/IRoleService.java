package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IRoleService {
    public EntityResult roleQuery(Map<?, ?> keyMap, List<?> attrList);
    public EntityResult roleInsert(Map<String, Object> attrMap);
    public EntityResult roleDelete(Map<?,?>keyMap);
}
