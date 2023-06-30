package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface ICriteriaService {
    public EntityResult criteriaQuery(Map<?,?> keyMap, List<String> attrList);
}
