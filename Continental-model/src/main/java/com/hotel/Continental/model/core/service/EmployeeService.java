package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IEmployeeService;
import com.hotel.continental.api.core.service.IUserService;
import com.hotel.continental.model.core.dao.EmployeeDao;
import com.hotel.continental.model.core.dao.HotelDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Lazy
@Service("EmployeeService")
public class EmployeeService implements IEmployeeService {

    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    HotelDao hotelDao;
    @Autowired
    DefaultOntimizeDaoHelper daoHelper;

    @Override
    public EntityResult employeeInsert(Map<?, ?> attrMap) {
        if (!attrMap.containsKey(EmployeeDao.EMPLOYMENT) || !attrMap.containsKey(EmployeeDao.IDHOTEL)
                || attrMap.containsKey(EmployeeDao.DOCUMENT) || !attrMap.containsKey(EmployeeDao.NAME)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
        }
        
        // Comprueba que el hotel existe

        Map<String, Object> filter = new HashMap<>();
        filter.put(HotelDao.ID, attrMap.get(EmployeeDao.IDHOTEL));
        EntityResult hotel = this.daoHelper.query(this.hotelDao, filter, Arrays.asList(HotelDao.ID));

        if (hotel.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.HOTEL_NOT_EXIST);
            return er;
        }
//Check que no existe el nif
        filter = new HashMap<>();
        filter.put(EmployeeDao.DOCUMENT, attrMap.get(EmployeeDao.DOCUMENT));
        EntityResult nif = this.daoHelper.query(this.employeeDao, filter, Arrays.asList(EmployeeDao.DOCUMENT));

        if (nif.calculateRecordNumber() > 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.DOCUMENT_ALREADY_EXIST);
            return er;
        }
        return this.daoHelper.insert(this.employeeDao, attrMap);
    }
}
