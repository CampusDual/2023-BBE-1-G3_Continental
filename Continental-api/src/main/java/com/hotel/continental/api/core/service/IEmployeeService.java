package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;
import java.util.Map;

public interface IEmployeeService {
    public EntityResult employeeInsert(Map<?, ?> attrMap);
    public EntityResult employeeDelete(Map<?, ?> keyMap);
    public EntityResult employeeUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap);
}
