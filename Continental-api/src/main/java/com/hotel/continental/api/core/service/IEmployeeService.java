package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IEmployeeService {
    public EntityResult employeeInsert(Map<?, ?> attrMap);
    public EntityResult employeeQuery(Map<?,?> keyMap, List<?> attrList);
    public EntityResult employeeDelete(Map<?, ?> keyMap);
}
