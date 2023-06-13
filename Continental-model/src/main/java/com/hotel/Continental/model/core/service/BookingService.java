package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IBookingService;
import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Lazy
@Service("BookingService")
public class BookingService implements IBookingService {
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private RoomService roomService;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    /**
     * Metodo que devuelve las reservas
     *
     * @param keyMap   Mapa con los campos de la clave
     * @param attrList Lista de atributos que se quieren devolver
     * @return EntityResult con las reservas o un mensaje de error
     */
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

    public EntityResult bookingInsert(Map<String, Object> attrMap) {
        if (attrMap.get(BookingDao.STARTDATE) == null || attrMap.get(BookingDao.ENDDATE) == null ||
                attrMap.get(BookingDao.CLIENT) == null) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        String initialDateString = attrMap.remove(BookingDao.STARTDATE).toString();
        String finalDateString = attrMap.remove(BookingDao.ENDDATE).toString();
        Date initialDate = getDateFromString(initialDateString);
        Date finalDate = getDateFromString(finalDateString);
        attrMap.put(BookingDao.STARTDATE, initialDate);
        attrMap.put(BookingDao.ENDDATE, finalDate);
        if (finalDate == null || initialDate == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.DATE_FORMAT_ERROR);
            return er;
        }
        if (finalDate.before(initialDate)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.FINAL_DATE_BEFORE_INITIAL_DATE);
            return er;
        }
        //Comprobar si la habitacion esta libre usando la fecha de inicio y fin de la reserva el id de habitacion
        //Si esta libre se inserta
        //Si no esta libre se devuelve un error
        List<String> roomKeyMap = new ArrayList<>();
        roomKeyMap.add(RoomDao.IDHABITACION);
        Map<String, Object> roomAttrMap = new HashMap<>();
        roomAttrMap.put("initialdate", initialDateString);
        roomAttrMap.put("finaldate", finalDateString);
        if (attrMap.get(BookingDao.ROOMID) != null) {
            roomAttrMap.put(BookingDao.ROOMID, attrMap.get(RoomDao.IDHABITACION));
        }
        EntityResult habitacionesLibres = roomService.freeRoomsQuery(roomAttrMap, roomKeyMap);//Todas las habitaciones libres entre esas dos fechas
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
        if (book == null || book.getCode() == EntityResult.OPERATION_WRONG) {
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
    public EntityResult bookingUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_WRONG);
        //Comprobamos que se ha introducido el id de la reserva
        if (keyMap.get(BookingDao.BOOKINGID) == null) {
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos si la reserva existe
        EntityResult book = this.daoHelper.query(this.bookingDao, keyMap, List.of(BookingDao.BOOKINGID));
        if (book == null || book.getCode() == EntityResult.OPERATION_WRONG) {
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        if (attrMap.get(BookingDao.STARTDATE) == null || attrMap.get(BookingDao.ENDDATE) == null) {
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Guardamos las fechas en variables para poder compararlas
        Date initialDate = getDateFromString(attrMap.remove(BookingDao.STARTDATE).toString());
        Date finalDate = getDateFromString(attrMap.remove(BookingDao.ENDDATE).toString());
        if (finalDate == null || initialDate == null) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            if (finalDate == null || initialDate == null) {
                er.setMessage(ErrorMessages.DATE_FORMAT_ERROR);
                return er;
            }
            if (finalDate.before(initialDate)) {
                er.setMessage(ErrorMessages.FINAL_DATE_BEFORE_INITIAL_DATE);
                return er;
            }
        }
        //actualizamos la reserva
        attrMap.put(BookingDao.STARTDATE, initialDate);
        attrMap.put(BookingDao.ENDDATE, finalDate);
        return this.daoHelper.update(this.bookingDao, attrMap, keyMap);
    }

    private static Date getDateFromString(String dateString) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            date = formatter.parse(dateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }
}
