package com.hotel.continental.model.core.service;

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
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult bookingCheckin(Map<String, Object> attrMap) {
        //Comprobamos que se ha introducido el id de la reserva
        //Si se introduce id de reserva se usa id de reserva, si se introduce id de cliente,o se usa el id de cliente
        //Si tiene multiples reservas se solicita el id de reserva
        if (attrMap.get(BookingDao.BOOKINGID) == null) {
            //Comprobamos que se ha introducido el id de cliente
            if (attrMap.get(BookingDao.CLIENT) == null) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.NECESSARY_KEY);
                return er;
            }
            //si se ha introducido el id de cliente
            Map<String, Object> filtroReservas = new HashMap<>();
            filtroReservas.put(BookingDao.CLIENT, attrMap.get(BookingDao.CLIENT));
            //Si se introduce la start date, se usa, sino se usa la fecha actual
            if (attrMap.get(BookingDao.STARTDATE) != null) {
                //Parseamos la cadena a fecha
                try {
                    LocalDate date = LocalDate.parse((String) attrMap.remove(BookingDao.STARTDATE));
                    filtroReservas.put(BookingDao.STARTDATE, date);
                }catch (DateTimeParseException e) {
                    EntityResult er = new EntityResultMapImpl();
                    er.setCode(EntityResult.OPERATION_WRONG);
                    er.setMessage(ErrorMessages.DATE_FORMAT_ERROR);
                    return er;
                }

            } else {
                //Si no se introduce la fecha se usa la fecha actual
                filtroReservas.put(BookingDao.STARTDATE, LocalDate.now());
            }
            EntityResult book = this.daoHelper.query(this.bookingDao, filtroReservas, List.of(BookingDao.BOOKINGID));
            //Si el numero de reservas es 0 se lanza el error de que no se encontro ninguna reserva con esa
            //informacion, si es mayor que 1 se solicita el id de reserva
            if (book.calculateRecordNumber() == 0) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
                return er;
            } else if (book.calculateRecordNumber() > 1) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.MORE_THAN_ONE_BOOKING);
                return er;
            }
            attrMap.put(BookingDao.BOOKINGID, book.getRecordValues(0).get(BookingDao.BOOKINGID));
        }
        //Comprobamos si la reserva existe
        EntityResult book = this.daoHelper.query(this.bookingDao, attrMap, List.of(BookingDao.BOOKINGID, BookingDao.CHECKIN_DATETIME));
        if (book.getRecordValues(0).isEmpty() || book.getCode() == EntityResult.OPERATION_WRONG) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
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
    public EntityResult bookingCheckout(Map<String, Object> attrMap) {
        //Comprobamos que se ha introducido el id de la reserva);
        //Si se introduce id de reserva se usa id de reserva, si se introduce id de cliente, se usa el id de cliente
        //Si tiene multiples reservas se solicita el id de reserva
        if (attrMap.get(BookingDao.BOOKINGID) == null) {
            //Comprobamos que se ha introducido el id de cliente
            if (attrMap.get(BookingDao.CLIENT) == null) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.NECESSARY_KEY);
                return er;
            }

            //si se ha introducido el id de cliente  se busca el id de reserva
            Map<String, Object> filtroReservas = new HashMap<>();
            filtroReservas.put(BookingDao.CLIENT, attrMap.get(BookingDao.CLIENT));
            if (attrMap.get(BookingDao.ENDDATE) != null) {
                //Parseamos la cadena a fecha
                try {
                    LocalDate date = LocalDate.parse((String) attrMap.remove(BookingDao.ENDDATE));
                    filtroReservas.put(BookingDao.ENDDATE, date);
                }catch (DateTimeParseException e) {
                    EntityResult er = new EntityResultMapImpl();
                    er.setCode(EntityResult.OPERATION_WRONG);
                    er.setMessage(ErrorMessages.DATE_FORMAT_ERROR);
                    return er;
                }

            } else {
                //Si no se introduce la fecha se usa la fecha actual
                filtroReservas.put(BookingDao.ENDDATE, LocalDate.now());
            }
            EntityResult book = this.daoHelper.query(this.bookingDao, filtroReservas, List.of(BookingDao.BOOKINGID));
            //Si el numero de reservas es distinto de 1 se solicita el id de reserva
            if (book.calculateRecordNumber() != 1) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.MORE_THAN_ONE_BOOKING);
                return er;
            }
            attrMap.put(BookingDao.BOOKINGID, book.getRecordValues(0).get(BookingDao.BOOKINGID));
        }
        //Comprobamos si la reserva existe
        EntityResult book = this.daoHelper.query(this.bookingDao, attrMap, List.of(BookingDao.BOOKINGID, BookingDao.CHECKIN_DATETIME, BookingDao.CHECKOUT_DATETIME));
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
