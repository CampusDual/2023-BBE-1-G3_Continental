package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.Map;

public interface IAccessCardAssignment {
    public EntityResult accesscardassignmentInsert(Map<String, Object> attrMap);
    public EntityResult accesscardassignmentRecover(Map<?, ?> attrMap);
}
