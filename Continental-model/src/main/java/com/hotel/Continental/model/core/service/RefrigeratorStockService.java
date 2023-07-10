package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRefrigeratorStockService;
import com.hotel.continental.model.core.dao.RefrigeratorStockDao;
import com.hotel.continental.model.core.dao.RefrigeratorsDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("RefrigeratorStockService")
public class RefrigeratorStockService implements IRefrigeratorStockService {
    @Autowired
    RefrigeratorStockDao refrigeratorStockDao;
    @Autowired
    RefrigeratorsDao refrigeratorsDao;
    @Autowired
    ProductsDao productDao;
    @Autowired
    DefaultOntimizeDaoHelper daoHelper;
    
    @Override
    public EntityResult refrigeratorDefaultUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        //attrMap = productid, stock
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        if (keyMap.get(RefrigeratorStockDao.PRODUCTID) == null || attrMap.get(RefrigeratorStockDao.STOCK) == null) {
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        Map<String, Object> filterProduct = new HashMap<>();
        filterProduct.put(RefrigeratorStockDao.REFRIGERATORID, -1);
        filterProduct.put(RefrigeratorStockDao.PRODUCTID, keyMap.get(RefrigeratorStockDao.PRODUCTID));
        //Obtenemos si ya existe ese producto en la nevera default, si no lo añadimos
        EntityResult stock = this.daoHelper.query(this.refrigeratorStockDao, filterProduct, List.of(RefrigeratorStockDao.STOCKID));
        if (stock.calculateRecordNumber() == 0) {
            filterProduct.put(RefrigeratorStockDao.STOCK, attrMap.get(RefrigeratorStockDao.STOCK));
            return this.daoHelper.insert(this.refrigeratorStockDao, filterProduct);
        }
        EntityResult stockid = this.daoHelper.query(this.refrigeratorStockDao, filterProduct, List.of(RefrigeratorStockDao.STOCKID));
        Map<String, Object> mapStockid = new HashMap<>();
        mapStockid.put(RefrigeratorStockDao.STOCKID, stockid.getRecordValues(0).get(RefrigeratorStockDao.STOCKID));

        return this.daoHelper.update(this.refrigeratorStockDao, attrMap, mapStockid);
    }
}