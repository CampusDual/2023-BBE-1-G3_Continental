package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.AccessCardAssignmentDao;
import com.hotel.continental.model.core.dao.AccessCardDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.hotel.continental.api.core.service.IBookingService;
import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("BookingService")
public class BookingService implements IBookingService {

    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private RoomService roomService;
    @Autowired
    private AccessCardAssignmentService accessCardAssignmentService;
    @Autowired
    private AccessCardAssignmentDao accessCardAssignmentDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    public static final String INITIALDATE = "initialdate";
    public static final String FINALDATE = "finaldate";

    /**
     * Metodo que devuelve las reservas
     *
     * @param keyMap   Mapa con los campos de la clave
     * @param attrList Lista de atributos que se quieren devolver
     * @return EntityResult con las reservas o un mensaje de error
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult bookingQuery(Map<?, ?> keyMap, List<?> attrList) {
        EntityResult result = this.daoHelper.query(this.bookingDao, keyMap, attrList);
        if (result == null || result.calculateRecordNumber() == 0) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        return result;
    }

    /**
     * Metodo que inserta una reserva
     *
     * @param attrMap Mapa con los campos de la reserva
     * @return EntityResult con la reserva insertada o un mensaje de error
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult bookingInsert(Map<String, Object> attrMap) {
        //Comprobar que se han introducido los datos necesarios
        //Primero comprobamos que nos envian startdate, enddate y client

        if (attrMap.get(BookingDao.STARTDATE) == null || attrMap.get(BookingDao.ENDDATE) == null ||
                attrMap.get(BookingDao.CLIENT) == null) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Comprobar si la habitacion esta libre usando la fecha de inicio y fin de la reserva el id de habitacion
        //Si esta libre se inserta
        //Si no esta libre se devuelve un error
        List<String> roomKeyMap = new ArrayList<>();
        roomKeyMap.add(RoomDao.IDHABITACION);
        Map<String, Object> roomAttrMap = new HashMap<>();
        roomAttrMap.put(INITIALDATE, attrMap.get(BookingDao.STARTDATE));
        roomAttrMap.put(FINALDATE, attrMap.get(BookingDao.ENDDATE));
        //Si se nos envia el id de la habitacion se busca esa habitacion

        if (attrMap.get(BookingDao.ROOMID) != null) {
            roomAttrMap.put(BookingDao.ROOMID, attrMap.get(RoomDao.IDHABITACION));
        }
        EntityResult habitacionesLibres = roomService.freeRoomsQuery(roomAttrMap, roomKeyMap);//Todas las habitaciones libres entre esas dos fechas
        //Comprobar que no dio error
        if (habitacionesLibres.getCode() == EntityResult.OPERATION_WRONG) {
            return habitacionesLibres;
        }
        //Si hay habitaciones libres se busca si esa habitacion esta libre en esas fechas
        //Si esta libre se inserta
        //Si no esta libre se devuelve un error
        if (habitacionesLibres.calculateRecordNumber() > 0) {
            //Buscamos si la habitacion esta libre en esas fechas
            Map<String, Object> room = habitacionesLibres.getRecordValues(0);
            attrMap.put(BookingDao.ROOMID, room.get(RoomDao.IDHABITACION));
            return this.daoHelper.insert(this.bookingDao, attrMap);
        }
        EntityResult er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_WRONG);
        er.setMessage(ErrorMessages.ROOM_NOT_FREE);
        return er;

    }

    /**
     * Metodo que borra una reserva
     *
     * @param keyMap Mapa con los campos de la clave
     * @return EntityResult con la reserva borrada o un mensaje de error
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult bookingDelete(Map<?, ?> keyMap) {
        //Comprobamos si se nos envia el id
        if (keyMap.get(BookingDao.BOOKINGID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos si la reserva existe
        EntityResult book = this.daoHelper.query(this.bookingDao, keyMap, List.of(BookingDao.BOOKINGID));
        if (book == null || book.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        return this.daoHelper.delete(this.bookingDao, keyMap);
    }

    /**
     * Metodo que actualiza una reserva
     *
     * @param attrMap Mapa con los campos a actualizar
     * @param keyMap  Mapa con los campos de la clave
     * @return EntityResult con la reserva actualizada o un mensaje de error
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })

    public EntityResult bookingUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        //Comprobamos que se ha introducido el id de la reserva
        if (keyMap.get(BookingDao.BOOKINGID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos si la reserva existe
        EntityResult book = this.daoHelper.query(this.bookingDao, keyMap, List.of(BookingDao.BOOKINGID));
        if (book == null || book.getCode() == EntityResult.OPERATION_WRONG) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        //Si se envia alguna fecha comprobamos que se envian ambas
        if (attrMap.get(BookingDao.STARTDATE) != null || attrMap.get(BookingDao.ENDDATE) != null) {
            if (attrMap.get(BookingDao.STARTDATE) == null || attrMap.get(BookingDao.ENDDATE) == null) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.NECESSARY_DATA);
                return er;
            }
            //Comprobamos si la habitacion esta libre usando la fecha de inicio y fin de la reserva el id de habitacion
            //Si esta libre se actualiza
            //Si no esta libre se devuelve un error
            List<String> roomKeyMap = new ArrayList<>();
            roomKeyMap.add(RoomDao.IDHABITACION);
            Map<String, Object> roomAttrMap = new HashMap<>();
            roomAttrMap.put(INITIALDATE, attrMap.get(BookingDao.STARTDATE));
            roomAttrMap.put(FINALDATE, attrMap.get(BookingDao.ENDDATE));
            //Si se nos envia el id de la habitacion se busca esa habitacion
            if (attrMap.get(BookingDao.ROOMID) != null) {
                roomAttrMap.put(BookingDao.ROOMID, attrMap.get(RoomDao.IDHABITACION));
            }
            EntityResult habitacionesLibres = roomService.freeRoomsQuery(roomAttrMap, roomKeyMap);//Todas las habitaciones libres entre esas dos fechas
            //Comprobar que no dio error
            if (habitacionesLibres.getCode() == EntityResult.OPERATION_WRONG) {
                return habitacionesLibres;
            }
            //Si hay habitaciones libres se busca si esa habitacion esta libre en esas fechas
            //Si esta libre se actualiza
            //Si no esta libre se devuelve un error
            if (habitacionesLibres.calculateRecordNumber() == 0) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.ROOM_NOT_FREE);
                return er;
            }
            //Buscamos si la habitacion esta libre en esas fechas
            Map<String, Object> room = habitacionesLibres.getRecordValues(0);
            attrMap.put(BookingDao.ROOMID, room.get(RoomDao.IDHABITACION));
            attrMap.remove(BookingDao.STARTDATE);
            attrMap.remove(BookingDao.ENDDATE);
            attrMap.put(BookingDao.STARTDATE, roomAttrMap.get(INITIALDATE));
            attrMap.put(BookingDao.ENDDATE, roomAttrMap.get(FINALDATE));
        }
        return this.daoHelper.update(this.bookingDao, attrMap, keyMap);
    }

    @Override
    //@Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult bookingCheckin(Map<String, Object> attrMap) {
        //Comprobamos que se ha introducido el id de la reserva
        if (attrMap.get(BookingDao.BOOKINGID) == null) {
            //Comprobamos que se ha introducido el id de cliente
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.NECESSARY_KEY);
                return er;
        }
        //Comprobamos si la reserva existe
        Map<String, Object> filterId = new HashMap<>();
        filterId.put(BookingDao.BOOKINGID, attrMap.get(BookingDao.BOOKINGID));
        EntityResult book = this.daoHelper.query(this.bookingDao, filterId, List.of(BookingDao.BOOKINGID, BookingDao.CHECKIN_DATETIME));
        if (book.getRecordValues(0).isEmpty() || book.getCode() == EntityResult.OPERATION_WRONG) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        //Comprobamos que la reserva pertenece al cliente
        Map<String, Object> filter = new HashMap<>();
        filter.put(BookingDao.BOOKINGID, attrMap.get(BookingDao.BOOKINGID));
        filter.put(BookingDao.CLIENT, attrMap.get(BookingDao.CLIENT));
        EntityResult bookclient = this.daoHelper.query(this.bookingDao, filter, List.of(BookingDao.BOOKINGID, BookingDao.CLIENT));
        if (bookclient.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.BOOKING_DOESNT_BELONG_CLIENT);
            return er;
        }
        //Comprobamos que no se ha hecho el checkin
        if (book.getRecordValues(0).get(BookingDao.CHECKIN_DATETIME) != null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_ALREADY_CHECKED_IN);
            return er;
        }
        //Update de la reserva
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put(BookingDao.BOOKINGID, attrMap.get(BookingDao.BOOKINGID));
        Map<String, Object> attrMapUpdate = new HashMap<>();
        attrMapUpdate.put(BookingDao.CHECKIN_DATETIME, LocalDateTime.now());
        EntityResult er = this.daoHelper.update(this.bookingDao, attrMapUpdate, keyMap);
        er.setMessage(ErrorMessages.BOOKING_CHECK_IN_SUCCESS);
        return er;
    }

    @Override
    //@Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult bookingCheckout(Map<String, Object> attrMap) {
        //Comprobamos que se ha introducido el id de la reserva);
        //Si se introduce id de reserva se usa id de reserva
        if (attrMap.get(BookingDao.BOOKINGID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos si la reserva existe
        Map<String, Object> filterId = new HashMap<>();
        filterId.put(BookingDao.BOOKINGID, attrMap.get(BookingDao.BOOKINGID));
        EntityResult book = this.daoHelper.query(this.bookingDao, filterId, List.of(BookingDao.BOOKINGID, BookingDao.CHECKIN_DATETIME, BookingDao.CHECKOUT_DATETIME));
        if (book.getRecordValues(0).isEmpty() || book.getCode() == EntityResult.OPERATION_WRONG) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        
        //Comprobamos que se haya hecho el checkin
        if (book.getRecordValues(0).get(BookingDao.CHECKIN_DATETIME) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_CHECKED_IN);
            return er;
        }
        //Comprobamos que no se ha hecho el checkout
        if (book.getRecordValues(0).get(BookingDao.CHECKOUT_DATETIME) != null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_ALREADY_CHECKED_OUT);
            return er;
        }

        //Comprobamos que la reserva pertenece al cliente
        Map<String, Object> filter = new HashMap<>();
        filter.put(BookingDao.BOOKINGID, attrMap.get(BookingDao.BOOKINGID));
        filter.put(BookingDao.CLIENT, attrMap.get(BookingDao.CLIENT));
        EntityResult bookclient = this.daoHelper.query(this.bookingDao, filter, List.of(BookingDao.BOOKINGID, BookingDao.CLIENT));
        if (bookclient.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.BOOKING_DOESNT_BELONG_CLIENT);
            return er;
        }
        //Comprobamos que la tarjeta pertenece a la reserva
        Map<String, Object> filterCard = new HashMap<>();
        filterCard.put(AccessCardDao.ACCESSCARDID, attrMap.get(AccessCardDao.ACCESSCARDID));
        filterCard.put(BookingDao.BOOKINGID, attrMap.get(BookingDao.BOOKINGID));
        EntityResult card = this.daoHelper.query(this.accessCardAssignmentDao, filterCard, List.of(AccessCardDao.ACCESSCARDID, BookingDao.BOOKINGID));
        if (card.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.CARD_DOESNT_BELONG_BOOKING);
            return er;
        }
        //Update de la tarjeta
        Map<String, Object> keyMapCard = new HashMap<>();
        keyMapCard.put(AccessCardDao.ACCESSCARDID, attrMap.get(AccessCardDao.ACCESSCARDID));
        EntityResult erTarjeta=accessCardAssignmentService.accesscardassignmentRecover(keyMapCard);
        if(erTarjeta.getCode()==EntityResult.OPERATION_WRONG){
            return erTarjeta;
        }
        //Update de la reserva
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put(BookingDao.BOOKINGID, attrMap.get(BookingDao.BOOKINGID));
        Map<String, Object> attrMapUpdate = new HashMap<>();
        attrMapUpdate.put(BookingDao.CHECKOUT_DATETIME, LocalDateTime.now());
        EntityResult er = this.daoHelper.update(this.bookingDao, attrMapUpdate, keyMap);
        er.setMessage(ErrorMessages.BOOKING_CHECK_OUT_SUCCESS);
        return er;
    }
}
