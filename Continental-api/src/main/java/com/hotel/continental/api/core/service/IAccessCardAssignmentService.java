package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.Map;

public interface IAccessCardAssignmentService {
    public EntityResult accesscardassignmentInsert(Map<String, Object> attrMap);
}
