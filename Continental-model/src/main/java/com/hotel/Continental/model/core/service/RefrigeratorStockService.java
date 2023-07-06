package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRefrigeratorStockService;
import com.hotel.continental.model.core.dao.ProductsDao;
import com.hotel.continental.model.core.dao.RefrigeratorStockDao;
import com.hotel.continental.model.core.dao.RefrigeratorsDao;
import com.hotel.continental.model.core.dao.RoomDao;
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
    public EntityResult refrigeratorStockInsert(Map<?, ?> attrMap) {
        if(attrMap.get(RefrigeratorStockDao.REFRIGERATORID) == null || attrMap.get(RefrigeratorStockDao.STOCK) == null ||
            String.valueOf(attrMap.get(RefrigeratorStockDao.REFRIGERATORID)).isBlank() || String.valueOf(attrMap.get(RefrigeratorStockDao.STOCK)).isBlank() || attrMap.get(RefrigeratorStockDao.PRODUCTID) == null){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }

        //Compruebo que la cantidad es númerica
        try{
            Integer.parseInt(String.valueOf(attrMap.get(RefrigeratorStockDao.STOCK)));
        } catch (NumberFormatException e){
            //Compruebo que la capacidad es mayor que 0
            if (Integer.parseInt(attrMap.get(RefrigeratorStockDao.STOCK).toString()) <= 0) {
                EntityResult erError = new EntityResultMapImpl();
                erError.setCode(EntityResult.OPERATION_WRONG);
                erError.setMessage(ErrorMessages.QUANTITY_NOT_POSITIVE);
                return erError;
            }
        }
        return this.daoHelper.insert(this.refrigeratorStockDao, attrMap);
    }

    @Override
    public EntityResult refrigeratorStockQuery(Map<?, ?> keyMap, List<?> attrList) {
        if(attrList.isEmpty()) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        EntityResult stock = this.daoHelper.query(this.refrigeratorStockDao, keyMap, attrList);
        if (stock.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.STOCK_NOT_EXIST);
            return er;
        }
        return stock;
    }

    @Override
    public EntityResult refrigeratorStockDelete(Map<?, ?> keyMap) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        if (keyMap.get(RefrigeratorStockDao.STOCKID) == null) {
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        if (((Integer)keyMap.get(RefrigeratorStockDao.REFRIGERATORID)) == -1) {
            er.setMessage(ErrorMessages.REFRIGERATOR_BLOCKED);
            return er;
        }
        EntityResult stock = this.daoHelper.query(this.refrigeratorStockDao, keyMap, List.of(RefrigeratorStockDao.STOCKID));
        if (stock.calculateRecordNumber()==0) {
            er.setMessage(ErrorMessages.STOCK_NOT_EXIST);
            return er;
        }
        return this.daoHelper.delete(this.refrigeratorStockDao, keyMap);
    }

    @Override
    public EntityResult refrigeratorStockMinusOne(Map<?, ?> attrMap) {
        //attrMap = Productid, Refrigeratorid
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        if (attrMap.get(RefrigeratorStockDao.REFRIGERATORID) == null || attrMap.get(RefrigeratorStockDao.PRODUCTID) == null) {
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        EntityResult stock = this.daoHelper.query(this.refrigeratorStockDao, attrMap, List.of(RefrigeratorStockDao.STOCKID));
        if (stock.calculateRecordNumber() == 0) {
            er.setMessage(ErrorMessages.STOCK_NOT_EXIST);
            return er;
        }
        return null;
    }

    @Override
    public EntityResult refillStock(Map<?, ?> keyMap) {
        //attrMap = refrigeratorid, productid
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        if (keyMap.get(RefrigeratorStockDao.REFRIGERATORID) == null || keyMap.get(RefrigeratorStockDao.PRODUCTID) == null ||
        keyMap.get(RefrigeratorStockDao.STOCK) == null) {
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        Map<String, Object> refrigeratorFilter = new HashMap<>();
        refrigeratorFilter.put(RefrigeratorStockDao.PRODUCTID, keyMap.get(RefrigeratorStockDao.PRODUCTID));
        refrigeratorFilter.put(RefrigeratorStockDao.REFRIGERATORID, keyMap.get(RefrigeratorStockDao.REFRIGERATORID))
        EntityResult stock = this.daoHelper.query(this.refrigeratorsDao, refrigeratorFilter, List.of(RefrigeratorsDao.FRIDGE_ID));
        if (stock.calculateRecordNumber() == 0) {
            er.setMessage(ErrorMessages.STOCK_NOT_EXIST);
            return er;
        }
        //Sacar diferencia stock default
        Map<String,Object> filter = new HashMap<>();
        filter.put(RefrigeratorStockDao.PRODUCTID, keyMap.get(RefrigeratorStockDao.PRODUCTID));
        filter.put(RefrigeratorStockDao.REFRIGERATORID, -1);
        EntityResult stockDefault = this.daoHelper.query(this.refrigeratorStockDao, filter, List.of(RefrigeratorStockDao.STOCK));
        if (stockDefault.calculateRecordNumber() == 0) {
            er.setMessage(ErrorMessages.PRODUCT_NOT_ON_DEFAULT_STOCK);
            return er;
        }
        EntityResult stockProduct = this.daoHelper.query(this.refrigeratorStockDao, refrigeratorFilter, List.of(RefrigeratorStockDao.STOCK));
        int stockRefrigerator = (Integer)stockProduct.get(RefrigeratorStockDao.STOCK);
        int stockRefrigeratorDefault =  (Integer)stockDefault.get(RefrigeratorStockDao.STOCK);
        int difference = stockRefrigeratorDefault - stockRefrigerator;

        Map<String, Object> data = new HashMap<>();
        data.put(RefrigeratorStockDao.STOCK, stockRefrigeratorDefault);
        Map<String, Object> filterUpdate = new HashMap<>();
        filterUpdate.put(RefrigeratorStockDao.REFRIGERATORID, keyMap.get(RefrigeratorStockDao.REFRIGERATORID));
        filterUpdate.put(RefrigeratorStockDao.PRODUCTID, keyMap.get(RefrigeratorStockDao.PRODUCTID));
        EntityResult result = this.daoHelper.update(this.refrigeratorStockDao, data, filterUpdate);
        result.put("Result", difference + "has been added");

        return result;
    }

    @Override
    public EntityResult refrigeratorDefaultUpdate(Map<String, Object> attrMap) {
        //attrMap = productid, stock
        //Si un hotel quiere cambiar los productos se lo cambia a todos, tabla refrigeratordefault con hotelid?
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        if (attrMap.get(RefrigeratorStockDao.PRODUCTID) == null || attrMap.get(RefrigeratorStockDao.STOCK) == null) {
            er.setMessage(ErrorMessages.NECESSARY_DATA);
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
        return this.daoHelper.update(this.refrigeratorStockDao, attrMap, keyMap);
    }
}
