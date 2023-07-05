package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IProductsService;
import com.hotel.continental.model.core.dao.ProductsDao;
import com.hotel.continental.model.core.dao.RoomTypeDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Lazy
@Service("ProductService")
public class ProductService implements IProductsService {
    @Autowired
    ProductsDao productsDao;
    @Autowired
    DefaultOntimizeDaoHelper daoHelper;

    @Override
    public EntityResult productsInsert(Map<?, ?> attrMap) {
        if (attrMap.get(ProductsDao.NAME) == null || attrMap.get(ProductsDao.PRICE) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        try {
            if (((Double) attrMap.get(ProductsDao.PRICE)) < 0) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(1);
                er.setMessage(ErrorMessages.PRICE_MINOR_0);
                return er;
            }
        } catch (ClassCastException e) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.PRICE_NOT_NUMBER);
            return er;
        }
        return this.daoHelper.insert(this.productsDao, attrMap);
    }

    @Override
    public EntityResult productsUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        if (keyMap.get(ProductsDao.PRODUCTID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        if (attrMap.isEmpty()) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        try {
            if (((Double) attrMap.get(ProductsDao.PRICE)) < 0) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(1);
                er.setMessage(ErrorMessages.PRICE_MINOR_0);
                return er;
            }
        } catch (ClassCastException e) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.PRICE_NOT_NUMBER);
            return er;
        }
        EntityResult er = new EntityResultMapImpl();
        e
    }

    @Override
    public EntityResult productsQuery(Map<?, ?> keyMap, List<?> attrList) {
        return null;
    }

    @Override
    public EntityResult productsDelete(Map<?, ?> keyMap) {
        return null;
    }
}
