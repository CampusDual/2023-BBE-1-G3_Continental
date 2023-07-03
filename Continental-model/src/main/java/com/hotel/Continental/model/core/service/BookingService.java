package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.*;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.hotel.continental.api.core.service.IBookingService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;

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
    @Autowired
    private RoomTypeDao roomTypeDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private CriteriaDao criteriaDao;
    @Autowired
    private SeasonDao seasonDao;
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
    @Secured({PermissionsProviderSecured.SECURED})
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
    @Secured({PermissionsProviderSecured.SECURED})
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
    @Secured({PermissionsProviderSecured.SECURED})
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
    @Secured({PermissionsProviderSecured.SECURED})
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

    /**
     * Metodo que realiza el checkin de una reserva
     *
     * @param attrMap
     * @return EntityResult
     */
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
        if (attrMap.get(AccessCardAssignmentDao.ACCESSCARDID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
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
        EntityResult card = accessCardAssignmentService.accesscardassignmentInsert(attrMap);
        if (card.getCode() == 1) {
            return card;
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

    /**
     * Metodo que realiza el checkout de una reserva
     *
     * @param attrMap
     * @return EntityResult
     */
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
        EntityResult erTarjeta = accessCardAssignmentService.accesscardassignmentRecover(keyMapCard);
        if (erTarjeta.getCode() == EntityResult.OPERATION_WRONG) {
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

    /**
     * TODO hacer calcular para dar el precio de una reserva a la hora de insertarla
     *      Que reciba el tipo de habitacion y las fechas y devuelva el precio
     *
     * @param attrMap
     * @return
     */
    @Override
    public EntityResult bookingPrice(Map<String, Object> attrMap) {
        //Obtener tipo de habitacion
        Map<String, Object> attrMapRoom = new HashMap<>();
        attrMapRoom.put(RoomDao.IDHABITACION, attrMap.get(BookingDao.ROOMID));
        EntityResult habitacion = this.daoHelper.query(this.roomDao, attrMapRoom, List.of(RoomDao.IDHABITACION, RoomDao.ROOMTYPEID, RoomDao.ROOMDOWNDATE));
        Map<String, Object> attrMapRoomType = new HashMap<>();
        attrMapRoomType.put(RoomTypeDao.TYPEID, habitacion.getRecordValues(0).get(RoomDao.ROOMTYPEID));
        EntityResult tipoHabitacion = this.daoHelper.query(this.roomTypeDao, attrMapRoomType, List.of(RoomTypeDao.TYPEID, RoomTypeDao.PRICE));
        //Obtener criterios de precio
        Map<String, Object> attrMapCriteria = new HashMap<>();
        EntityResult criteria = this.daoHelper.query(this.criteriaDao, attrMapCriteria, List.of(CriteriaDao.ID, CriteriaDao.NAME, CriteriaDao.MULTIPLIER));
        //Iteramos las fechas
        Date fechaInicio = null;
        Date fechaFin=null;
        try {
            fechaInicio = new SimpleDateFormat("yyyy-MM-dd").parse((String) attrMap.get(BookingDao.STARTDATE));
            fechaFin = new SimpleDateFormat("yyyy-MM-dd").parse((String) attrMap.get(BookingDao.ENDDATE));
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaInicio);

        while (!calendar.getTime().after(fechaFin)) {
            Date currentDate = calendar.getTime();
            //Comprobamos si es fin de semana
            boolean esFinde=isWeekend(currentDate);
            //Comprobamos que tipo de temporada es
            int Temproada=whatSeason(currentDate);
            calendar.add(Calendar.DATE, 1); // Avanza al siguiente día
        }
        //Descuento por reserva anticipada, si la fecha de inicio de la reserva es en mas de 10 dias
        //Si la fecha de inicio es dentro de mas de 10 dias añadimos ese criterio
        if(fechaInicio.after(Date.from(Instant.from(LocalDateTime.now().plusDays(10))))){
            System.out.println("Añadimos el criterio de reserva anticipada");
        }
        //Descuento por estancia larga, si la reserva es de mas de 5 dias
        if(ChronoUnit.DAYS.between(fechaInicio.toInstant(), fechaFin.toInstant()) > 5){
            System.out.println("Añadimos el criterio de estancia larga");
        }
        return null;
    }

    //Metodo que comprueba en que temporada esta la fecha
    private int whatSeason(Date date) {
        LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
        //Obtenemos las temporadas
        EntityResult seasons = this.daoHelper.query(this.seasonDao, Map.of(), List.of(SeasonDao.CRITERIA_ID, SeasonDao.START_DAY, SeasonDao.START_MONTH, SeasonDao.END_DAY, SeasonDao.END_MONTH));
        for (int i = 0; i < seasons.getRecordValues(0); i++) {
            int monthStart = (int) seasons.getRecordValues(i).get(SeasonDao.START_MONTH);
            int dayStart = (int) seasons.getRecordValues(i).get(SeasonDao.START_DAY);
            int monthEnd = (int) seasons.getRecordValues(i).get(SeasonDao.END_MONTH);
            int dayEnd = (int) seasons.getRecordValues(i).get(SeasonDao.END_DAY);
            if(localDate.getMonthValue()>=monthStart && localDate.getMonthValue()<=monthEnd){
                System.out.println("Estamos en la temporada "+seasons.getRecordValues(i).get(SeasonDao.CRITERIA_ID));
            }
        }
        return 0;
    }
    //Metodo que comprueba si la fecha es fin de semana
    private boolean isWeekend(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;
    }
}

