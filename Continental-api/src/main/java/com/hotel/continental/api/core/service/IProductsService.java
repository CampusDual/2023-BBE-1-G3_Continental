package com.hotel.continental.api.core.service;

import com.ontimize.jee.common.dto.EntityResult;

import java.util.List;
import java.util.Map;

public interface IProductsService {
    public EntityResult productsInsert(Map<?, ?> attrMap);
    public EntityResult productsUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap);
    public EntityResult productsQuery(Map<?,?> keyMap, List<?> attrList);
    public EntityResult productsDelete(Map<?, ?> keyMap);
}
