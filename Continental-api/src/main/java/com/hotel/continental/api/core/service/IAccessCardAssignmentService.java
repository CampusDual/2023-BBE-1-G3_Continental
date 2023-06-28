package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.Map;

public interface IAccessCardAssignmentService {
    public EntityResult accessCardAssignmentInsert(Map<String, Object> attrMap);
    public EntityResult accessCardAssignmentRecover(Map<String, Object> attrMap);
    public EntityResult lostCard(Map<String, Object> attrMap);
}
