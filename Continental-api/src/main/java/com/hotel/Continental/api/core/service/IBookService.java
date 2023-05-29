package com.hotel.Continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IBookService {
    public EntityResult bookInsert(Map<?,?> attrMap);
    public EntityResult bookDelete(Map<?,?> keyMap);
}
