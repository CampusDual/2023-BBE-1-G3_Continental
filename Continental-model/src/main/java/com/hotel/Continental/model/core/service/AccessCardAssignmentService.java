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
public class AccessCardAssignmentService implements IAccessCardAssignmentService {
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
        EntityResult accesscard = this.daoHelper.query(this.accessCardDao, keyMap, List.of(AccessCardDao.ACCESSCARDID, AccessCardDao.AVAILABLE, AccessCardDao.HOTELID));
        if (accesscard.calculateRecordNumber()==0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_EXIST);
            return er;
        }
        if (((List<Boolean>) accesscard.get(AccessCardDao.AVAILABLE)).get(0)==false) {
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
        keyMap.put(RoomDao.IDHABITACION, booking.getRecordValues(0).get(RoomDao.IDHABITACION));
        //Queremos que el hotel sea igual al de la reserva
        EntityResult roomhotel = this.daoHelper.query(this.roomDao, keyMap, List.of(RoomDao.IDHOTEL));
        boolean check = roomhotel.getRecordValues(0).get(RoomDao.IDHOTEL) == accesscard.getRecordValues(0).get(AccessCardDao.HOTELID);
        if (!check) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.HOTEL_INCORRECT);
            return er;
        }
        Map<String,Object> availablefalse = new HashMap<>();
        availablefalse.put(AccessCardDao.AVAILABLE, false);
        Map<String,Object> filterCards = new HashMap<>();
        filterCards.put(AccessCardDao.ACCESSCARDID, attrMap.get(AccessCardDao.ACCESSCARDID));
        this.daoHelper.update(this.accessCardDao, availablefalse, filterCards);
        this.daoHelper.insert(this.accessCardAssignmentDao, attrMap);
        EntityResult er = new EntityResultMapImpl();
        er.setCode(0);
        er.setMessage("The card " + attrMap.get(AccessCardDao.ACCESSCARDID) + " was given");
        return er;
    }
}
