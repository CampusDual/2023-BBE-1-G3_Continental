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
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.QUANTITY_NOT_NUMBER);
            return er;
        }
        //Compruebo que la capacidad es mayor que 0
        if (Integer.parseInt(attrMap.get(RefrigeratorStockDao.STOCK).toString()) <= 0) {
            EntityResult erError = new EntityResultMapImpl();
            erError.setCode(EntityResult.OPERATION_WRONG);
            erError.setMessage(ErrorMessages.QUANTITY_NOT_POSITIVE);
            return erError;
        }
        //Compruebo que el id de fridge es correcto

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
}
