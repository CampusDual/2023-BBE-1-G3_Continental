package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IExtraExpensesService;
import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.ExtraExpensesDao;
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
@Service("ExtraExpensesService")
public class ExtraExpensesService implements IExtraExpensesService {
    @Autowired
    ExtraExpensesDao extraExpensesDao;
    @Autowired
    BookingDao bookingDao;
    @Autowired
    DefaultOntimizeDaoHelper daoHelper;
    @Override
    public EntityResult extraexpensesInsert(Map<?, ?> attrMap) {
        if (attrMap.get(ExtraExpensesDao.BOOKINGID) == null || attrMap.get(ExtraExpensesDao.CONCEPT) == null || attrMap.get(ExtraExpensesDao.PRICE) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put(BookingDao.BOOKINGID, attrMap.get(ExtraExpensesDao.BOOKINGID));
        EntityResult bookings = this.daoHelper.query(this.bookingDao, keyMap, List.of(BookingDao.BOOKINGID));
        if (bookings.calculateRecordNumber()==0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        if (((String) attrMap.get(ExtraExpensesDao.CONCEPT)).isBlank()) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        return this.daoHelper.insert(this.extraExpensesDao, attrMap);

    }
}
