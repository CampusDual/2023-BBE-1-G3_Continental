package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRefrigeratorStockService;
import com.hotel.continental.model.core.dao.ExtraExpensesDao;
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
    @Autowired
    ExtraExpensesDao extraExpensesDao;
    
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

    @Override
    public EntityResult refrigeratorStockUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        //attrMap: quantity -> -1, 2 -3...
        //keyMap: refrigeratorid, productid
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        if (keyMap.get(RefrigeratorStockDao.REFRIGERATORID) == null || keyMap.get(RefrigeratorStockDao.PRODUCTID) == null) {
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        if (attrMap.get(RefrigeratorStockDao.STOCK) == null) {
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        if ((Integer) keyMap.get(RefrigeratorStockDao.REFRIGERATORID) == -1) {
            er.setMessage(ErrorMessages.REFRIGERATOR_BLOCKED);
            return er;
        }
        Map<String, Object> fridgemap = new HashMap<>();
        fridgemap.put(RefrigeratorsDao.FRIDGE_ID, keyMap.get(RefrigeratorStockDao.REFRIGERATORID));
        EntityResult existFridge = this.daoHelper.query(this.refrigeratorsDao, fridgemap, List.of(RefrigeratorsDao.FRIDGE_ID));
        if (existFridge.calculateRecordNumber() == 0) {
            er.setMessage(ErrorMessages.REFRIGERATOR_NOT_EXIST);
            return er;
        }
        Map<String, Object> productmap = new HashMap<>();
        productmap.put(RefrigeratorsDao.FRIDGE_ID, keyMap.get(RefrigeratorStockDao.REFRIGERATORID));
        EntityResult existproduct = this.daoHelper.query(this.refrigeratorsDao, productmap, List.of(RefrigeratorsDao.FRIDGE_ID));
        if (existproduct.calculateRecordNumber() == 0) {
            er.setMessage(ErrorMessages.PRODUCT_NOT_EXISTS);
            return er;
        }

        EntityResult stockid = this.daoHelper.query(this.refrigeratorStockDao, keyMap, List.of(RefrigeratorStockDao.STOCKID));
        Map<String, Object> keyMapDefault = new HashMap<>();
        keyMapDefault.putAll(keyMap);
        keyMapDefault.put(RefrigeratorStockDao.REFRIGERATORID, -1);
        EntityResult stockdefault = this.daoHelper.query(this.refrigeratorStockDao, keyMapDefault, List.of(RefrigeratorStockDao.STOCK));
        if (stockid.calculateRecordNumber() == 0 && stockdefault.calculateRecordNumber() == 0) {
            er.setMessage(ErrorMessages.PRODUCT_NOT_NECESSARY);
            return er;
        }
        int update = (Integer) attrMap.get(RefrigeratorStockDao.STOCK);
        EntityResult updateStock = this.daoHelper.query(this.refrigeratorStockDao, keyMap, List.of(RefrigeratorStockDao.STOCK));
        if (update == 0) {
            er.setMessage(ErrorMessages.UPDATE_STOCK_ZERO);
            return er;
        }
        if (updateStock.calculateRecordNumber() == 0) {
            Map<String, Object> data = new HashMap<>();
            data.putAll(attrMap);
            data.put(RefrigeratorStockDao.REFRIGERATORID, keyMap.get(RefrigeratorStockDao.REFRIGERATORID));
            data.put(RefrigeratorStockDao.PRODUCTID, keyMap.get(RefrigeratorStockDao.PRODUCTID));
            return this.daoHelper.insert(this.refrigeratorStockDao, data);
        }
        int stock = (Integer) updateStock.getRecordValues(0).get(RefrigeratorStockDao.STOCK);
        int newStock = stock + update;
        int defaultStock = (Integer) stockdefault.getRecordValues(0).get(RefrigeratorStockDao.STOCK);
        if (newStock > defaultStock) {
            er.setMessage(ErrorMessages.NEW_STOCK_HIGHER_THAN_DEFAULT);
            return er;
        }
        if (newStock < 0) {
            er.setMessage(ErrorMessages.NEW_STOCK_UNDER_ZERO);
            return er;
        }
        attrMap.put(RefrigeratorStockDao.STOCK, newStock);
        Map<String, Object> filter = new HashMap<>();
        filter.put(RefrigeratorStockDao.STOCKID, stockid.getRecordValues(0).get(RefrigeratorStockDao.STOCKID));
        return this.daoHelper.update(this.refrigeratorStockDao, attrMap, filter);
    }
}