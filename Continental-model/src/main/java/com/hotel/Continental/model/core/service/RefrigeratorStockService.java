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
    public EntityResult refrigeratorDefaultUpdate(Map<String, Object> attrMap) {
        //attrMap = productid, stock
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        if (attrMap.get(RefrigeratorStockDao.PRODUCTID) == null || attrMap.get(RefrigeratorStockDao.STOCK) == null) {
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        Map<String, Object> filter = new HashMap<>();
        filter.put(RefrigeratorStockDao.REFRIGERATORID, -1);
        filter.put(RefrigeratorStockDao.PRODUCTID, attrMap.get(RefrigeratorStockDao.PRODUCTID));
        //Obtenemos si ya existe ese producto en la nevera default, si no lo añadimos
        EntityResult stock = this.daoHelper.query(this.refrigeratorStockDao, filter, List.of(RefrigeratorStockDao.STOCKID));
        if (stock.calculateRecordNumber() == 0) {
            attrMap.put(RefrigeratorStockDao.REFRIGERATORID, -1);
            return this.daoHelper.insert(this.refrigeratorStockDao, attrMap);
        }
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put(RefrigeratorStockDao.REFRIGERATORID, -1);
        keyMap.put(RefrigeratorStockDao.PRODUCTID, attrMap.get(RefrigeratorStockDao.PRODUCTID));
        EntityResult stockid = this.daoHelper.query(this.refrigeratorStockDao, keyMap, List.of(RefrigeratorStockDao.STOCKID));
        keyMap = new HashMap<>();
        //Hay que recuperar el stockid
        keyMap.put(RefrigeratorStockDao.STOCKID, stockid.getRecordValues(0).get(RefrigeratorStockDao.STOCKID));
        Map<String, Object> data = new HashMap<>();
        data.put(RefrigeratorStockDao.STOCK, attrMap.get(RefrigeratorStockDao.STOCK));
        return this.daoHelper.update(this.refrigeratorStockDao, data, keyMap);
    }
}