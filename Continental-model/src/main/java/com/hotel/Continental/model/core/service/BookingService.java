package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.dao.*;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.hotel.continental.api.core.service.IBookingService;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicField;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private ExtraExpensesDao extraExpensesDao;
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
            attrMap.put(BookingDao.PRICE, this.bookingPrice(attrMap).getRecordValues(0).get(BookingDao.PRICE));
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
        EntityResult finalPrice = obtainFinalPrice(attrMap);
        //Update de la reserva
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put(BookingDao.BOOKINGID, attrMap.get(BookingDao.BOOKINGID));
        Map<String, Object> attrMapUpdate = new HashMap<>();
        attrMapUpdate.put(BookingDao.CHECKOUT_DATETIME, LocalDateTime.now());
        EntityResult er = this.daoHelper.update(this.bookingDao, attrMapUpdate, keyMap);
        er.setMessage(ErrorMessages.BOOKING_CHECK_OUT_SUCCESS);
        er.put(BookingDao.PRICE, List.of(finalPrice.get(BookingDao.PRICE)));
        return er;
    }

    /**
     *  Metodo que realiza el calculo del precio de una reserva
     * @param attrMap fecha de inicio y fin de la reserva y el id de la habitacion
     * @return EntityResult con el precio de la reserva
     */
    @Override
    //@Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult bookingPrice(Map<String, Object> attrMap) {
        double priceBooking = 0;
        //Comprobamos que se ha introducido el id de la habitacion
        if (attrMap.get(BookingDao.ROOMID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos que se ha introducido la fecha de inicio
        if (attrMap.get(BookingDao.STARTDATE) == null&&attrMap.get(BookingDao.ENDDATE) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Obtener tipo de habitacion
        Map<String, Object> attrMapRoom = new HashMap<>();
        attrMapRoom.put(RoomDao.IDHABITACION, attrMap.get(BookingDao.ROOMID));
        EntityResult room = this.daoHelper.query(this.roomDao, attrMapRoom, List.of(RoomDao.IDHABITACION, RoomDao.ROOMTYPEID, RoomDao.ROOMDOWNDATE));
        Map<String, Object> attrMapRoomType = new HashMap<>();
        attrMapRoomType.put(RoomTypeDao.TYPEID, room.getRecordValues(0).get(RoomDao.ROOMTYPEID));
        EntityResult roomtype = this.daoHelper.query(this.roomTypeDao, attrMapRoomType, List.of(RoomTypeDao.TYPEID, RoomTypeDao.PRICE));

        //Precio de la habitacion por dia
        double roomprice = (double) roomtype.getRecordValues(0).get(RoomTypeDao.PRICE);
        //Obtener criterios de precio
        Map<String, Object> attrMapCriteria = new HashMap<>();
        EntityResult criteria = this.daoHelper.query(this.criteriaDao, attrMapCriteria, List.of(CriteriaDao.ID, CriteriaDao.NAME, CriteriaDao.MULTIPLIER));

        //Hacer que obtenga los multiplicadores por nombre
        double multiplierWeekend= (float) criteria.getRecordValues(0).get(CriteriaDao.MULTIPLIER);
        double earlyBooking= (float) criteria.getRecordValues(1).get(CriteriaDao.MULTIPLIER);
        Map<Integer,Float> multiplierSeason=new HashMap<>();
        multiplierSeason.put(0, (float) 1.0);
        multiplierSeason.put((int)criteria.getRecordValues(2).get(CriteriaDao.ID),(float) criteria.getRecordValues(1).get(CriteriaDao.MULTIPLIER));
        multiplierSeason.put((int)criteria.getRecordValues(3).get(CriteriaDao.ID),(float) criteria.getRecordValues(2).get(CriteriaDao.MULTIPLIER));
        double multiplierLongStay= (float) criteria.getRecordValues(4).get(CriteriaDao.MULTIPLIER);

        //Obtener fechas de la reserva
        LocalDate start;
        LocalDate startIter;
        LocalDate end;
        try {
            start =  LocalDate.parse((String) attrMap.get(BookingDao.STARTDATE), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            end =  LocalDate.parse((String) attrMap.get(BookingDao.ENDDATE), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
            startIter=start;
        } catch (Exception e){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.DATE_FORMAT_ERROR);
            return er;
        }

        //Iterar por cada dia de la reserva y calcular el precio
        while (!startIter.isAfter(end)) {
            double extra = 0;
            if(isWeekend(startIter)){
                extra+=(multiplierWeekend*roomprice)-roomprice;
            }
            extra+=(roomprice*multiplierSeason.get(whatSeason(startIter))-roomprice);
            priceBooking+=roomprice+extra;
            startIter=startIter.plusDays(1);
        }

        //Descuento por reserva anticipada, si la fecha de inicio de la reserva es en mas de 10 dias
        //Si la fecha de inicio es dentro de mas de 10 dias añadimos ese criterio
        if(start.isAfter(LocalDate.now().plusDays(10))){
            priceBooking=priceBooking*earlyBooking;
        }
        //Descuento por estancia larga, si la reserva es de mas de 5 dias
        if(ChronoUnit.DAYS.between(start,end)>3){
            priceBooking=priceBooking*multiplierLongStay;
        }
        EntityResult er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        er.put(BookingDao.PRICE,priceBooking);
        return er;
    }

    //Metodo que comprueba en que temporada esta la fecha
    private int whatSeason(LocalDate localDate) {
        BasicField startDayField = new BasicField(SeasonDao.START_DAY);
        BasicField endDayField = new BasicField(SeasonDao.END_DAY);
        BasicField startMonthField = new BasicField(SeasonDao.START_MONTH);
        BasicField endMonthField = new BasicField(SeasonDao.END_MONTH);
        BasicExpression bexp1 = new BasicExpression(startDayField, BasicOperator.LESS_EQUAL_OP, localDate.getDayOfMonth());//s.start_day >=?1
        BasicExpression bexp2 = new BasicExpression(endDayField, BasicOperator.MORE_EQUAL_OP, localDate.getDayOfMonth());//s.end_day <=?2
        BasicExpression bexp3 = new BasicExpression(startMonthField, BasicOperator.LESS_EQUAL_OP, localDate.getMonthValue());//s.start_month>=?3
        BasicExpression bexp4 = new BasicExpression(endMonthField, BasicOperator.MORE_EQUAL_OP, localDate.getMonthValue());//s.end_month<=?4
        BasicExpression bexp5 = new BasicExpression(bexp1, BasicOperator.AND_OP, bexp2);//s.start_day >=?1 AND s.end_day <=?2
        BasicExpression bexp6 = new BasicExpression(bexp3, BasicOperator.AND_OP, bexp4);//s.start_month>=?3 AND s.end_month<=?4
        BasicExpression bexp7 = new BasicExpression(bexp5, BasicOperator.AND_OP, bexp6);//s.start_day >=?1 AND s.end_day <=?2 AND s.start_month>=?3 AND s.end_month<=?4
        Map<String, Object> filter = new HashMap<>();
        filter.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexp7);
        EntityResult er = this.daoHelper.query(this.seasonDao,filter, List.of(SeasonDao.CRITERIA_ID),SeasonDao.GET_SEASONS);
        if (er.calculateRecordNumber()>0){
            return (int) er.getRecordValues(0).get(SeasonDao.CRITERIA_ID);
        }
        return 0;
    }
    //Metodo que comprueba si la fecha es fin de semana
    private boolean isWeekend(LocalDate date) {
       return date.getDayOfWeek().equals(DayOfWeek.SATURDAY) || date.getDayOfWeek().equals(DayOfWeek.SUNDAY);
    }

    private EntityResult obtainFinalPrice(Map<String,Object> attrMap) {
        Map<String, Object> filter = new HashMap<>();
        filter.put(BookingDao.BOOKINGID, attrMap.get(BookingDao.BOOKINGID));
        EntityResult historic = this.daoHelper.query(this.extraExpensesDao, filter, List.of(ExtraExpensesDao.PRICE));
        double finalPrice = 0;
        Map<String, ArrayList<Double>> map = (Map<String, ArrayList<Double>>) historic;
        ArrayList<Double> list = new ArrayList<>(map.values().iterator().next());
        for (Double price : list) {
            finalPrice += price;
        }
        EntityResult bookingPrice = this.daoHelper.query(this.bookingDao, filter, List.of(BookingDao.PRICE));
        BigDecimal bookingcost = (BigDecimal)bookingPrice.getRecordValues(0).get(BookingDao.PRICE);
        BigDecimal doubleAsBigDecimal = BigDecimal.valueOf(finalPrice);
        BigDecimal resultado = bookingcost.add(doubleAsBigDecimal);

        EntityResult erFinalPrice = new EntityResultMapImpl();
        erFinalPrice.setCode(EntityResult.OPERATION_SUCCESSFUL);
        erFinalPrice.put(BookingDao.PRICE, resultado);
        return erFinalPrice;
    }
}

