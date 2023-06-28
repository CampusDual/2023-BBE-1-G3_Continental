package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IAccessCardAssignmentService;
import com.hotel.continental.model.core.dao.AccessCardAssignmentDao;
import com.hotel.continental.model.core.dao.AccessCardDao;
import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("AccessCardAssignmentService")
public class AccessCardAssignmentServiceService implements IAccessCardAssignmentService {
    @Autowired
    private AccessCardAssignmentDao accessCardAssignmentDao;
    @Autowired
    private AccessCardDao accessCardDao;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    @Override
    public EntityResult accesscardassignmentInsert(Map<String, Object> attrMap) {
        if (attrMap.get(AccessCardAssignmentDao.ACCESSCARDID) == null || attrMap.get(AccessCardAssignmentDao.BOOKINGID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        Map<String,Object> keyMap = new HashMap<>();
        keyMap.put(AccessCardDao.ACCESSCARDID, attrMap.get(AccessCardDao.ACCESSCARDID));
        EntityResult accesscard = this.daoHelper.query(this.accessCardDao, keyMap, List.of(AccessCardDao.ACCESSCARDID));
        if (accesscard.calculateRecordNumber()==0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_EXIST);
            return er;
        }
        keyMap.put(AccessCardDao.AVAILABLE, false);
        EntityResult query = this.daoHelper.query(this.accessCardDao, keyMap, List.of(AccessCardDao.ACCESSCARDID));
        if (query.calculateRecordNumber()>0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.ACCESS_CARD_ALREADY_GIVEN);
            return er;
        }
        keyMap = new HashMap<>();
        keyMap.put(BookingDao.BOOKINGID, attrMap.get(BookingDao.BOOKINGID));
        //Booking existe
        EntityResult booking = this.daoHelper.query(this.bookingDao, keyMap, List.of(BookingDao.ROOMID));
        if (booking.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        keyMap = new HashMap<>();
        keyMap.put(RoomDao.IDHABITACION, booking.get(BookingDao.ROOMID));
        //Queremos que el hotel sea igual al de la reserva
        EntityResult roomhotel = this.daoHelper.query(this.roomDao, keyMap, List.of(RoomDao.IDHOTEL));
        boolean check = roomhotel.get(RoomDao.IDHOTEL) == attrMap.get()

        return this.daoHelper.insert(this.accessCardAssignmentDao, attrMap);
    }
}
