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

import java.sql.Timestamp;
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
    @Override
    public EntityResult lostCard(Map<String, Object> attrMap) {
        //Compruebo que me da el id de la tarjeta
        if (attrMap.get(AccessCardAssignmentDao.ACCESSCARDID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }

        //Le cambiamos el estado a la tarjeta
        Map<String, Object> attrMapCard = new HashMap<>();
        attrMapCard.put(AccessCardDao.AVAILABLE, false);
        attrMapCard.put(AccessCardDao.HOTELID, null);
        attrMapCard.put(AccessCardDao.CARDDOWNDATE, new Timestamp(System.currentTimeMillis()));

        //Comprobamos si el update y el delete se hicieron correctamente
        EntityResult update = this.daoHelper.update(this.accessCardDao, attrMapCard, attrMap);

        if (update.getCode() == EntityResult.OPERATION_WRONG) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_RECOVERED);
            return er;
        }

        //Recogemos el id de la tabla accessCardAssigment y comprobamos que no está vacia
        EntityResult accessCardAssignment = this.daoHelper.query(this.accessCardAssignmentDao, attrMap, List.of(AccessCardAssignmentDao.ACCESSCARDASIGNMENT));

        if (accessCardAssignment.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_RECOVERED);
            return er;
        }

        Map<String, Object> attrMapAssignment = new HashMap<>();
        attrMapAssignment.put(AccessCardAssignmentDao.ACCESSCARDASIGNMENT, accessCardAssignment.getRecordValues(0).get(AccessCardAssignmentDao.ACCESSCARDASIGNMENT));

        update.setMessage(ErrorMessages.ACCESS_CARD_SUCCESSFULLY_MODIFY);
        return update;
    }

    @Override
    public EntityResult accesscardassignmentRecover(Map<?, ?> attrMap) {
        //Se comprueba si la tarjeta está perdida
        if(attrMap.get(AccessCardDao.CARDDOWNDATE) != null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.ACCESS_CARD_LOST);
            return er;
        }
        //Compruebo que me da el id de la tarjeta
        if (attrMap.get(AccessCardAssignmentDao.ACCESSCARDID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos que esa tarjeta existe,y comprobamos que esta asignada
        Map<String, Object> attrMapCard = new HashMap<>();
        attrMapCard.put(AccessCardDao.ACCESSCARDID, attrMap.get(AccessCardDao.ACCESSCARDID));
        EntityResult query = this.daoHelper.query(this.accessCardDao, attrMapCard, List.of(AccessCardDao.AVAILABLE));
        if(query.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_EXIST);
            return er;
        }
        if (!(boolean)query.getRecordValues(0).get(AccessCardDao.AVAILABLE)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ACCESS_CARD_ALREADY_GIVEN);
            return er;
        }
        //Comprobamos que esta asignada a esa reserva
        Map<String, Object> filter = new HashMap<>();
        filter.put(AccessCardAssignmentDao.ACCESSCARDID, attrMap.get(AccessCardAssignmentDao.ACCESSCARDID));
        filter.put(AccessCardAssignmentDao.BOOKINGID, attrMap.get(AccessCardAssignmentDao.BOOKINGID));
        EntityResult queryAccessCardAssignment = this.daoHelper.query(this.accessCardAssignmentDao, filter, List.of(AccessCardAssignmentDao.ACCESSCARDASIGNMENT));
        if (queryAccessCardAssignment.calculateRecordNumber()==0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.CARD_DOESNT_BELONG_BOOKING);
            return er;
        }
        //Le cambiamos el estado a la tarjeta
        Map<String, Object> attrMap2 = new HashMap<>();
        attrMap2.put(AccessCardDao.AVAILABLE, true);
        EntityResult update = this.daoHelper.update(this.accessCardDao, attrMap2, attrMap);
        if (update.getCode() == EntityResult.OPERATION_WRONG) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ACCESS_CARD_NOT_RECOVERED);
            return er;
        }
        return update;
    }
}
