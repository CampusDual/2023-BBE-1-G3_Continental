package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IParkingService;
import com.hotel.continental.model.core.dao.*;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicField;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.db.SQLStatementBuilder.ExtendedSQLConditionValuesProcessor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Lazy
@Service("ParkingService")
public class ParkingService implements IParkingService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private ParkingDao parkingDao;
    @Autowired
    private ParkingHistoryDao parkingHistoryDao;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private ParkingHistoryService parkingHistoryService;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private ExtraExpensesService extraExpensesService;
    @Autowired
    private HotelDao hotelDao;


    //@Override
    public EntityResult parkingEnter(Map<?, ?> attrMap) {
        EntityResult er = new EntityResultMapImpl();
        //Comprobar que me llega los datos necesarios para hacer la entrada id_booking id_parking
        if(attrMap.get(ParkingHistoryDao.ID_BOOKING) == null || attrMap.get(ParkingDao.ID_PARKING) == null){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Comprobar que hay sitio en el parking indicado
        Map<String, Object> attrMapParking = Map.of(ParkingDao.ID_PARKING, attrMap.get(ParkingDao.ID_PARKING));
        EntityResult erParking = this.daoHelper.query(parkingDao, attrMapParking, List.of(ParkingDao.OCCUPIED_CAPACITY,ParkingDao.TOTAL_CAPACITY,ParkingDao.ID_HOTEL));
        if(erParking.calculateRecordNumber() == 0){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.PARKING_NOT_FOUND);
            return er;
        }

        int occupiedCapacity = (int)erParking.getRecordValues(0).get(ParkingDao.OCCUPIED_CAPACITY);
        if(occupiedCapacity == (int)erParking.getRecordValues(0).get(ParkingDao.TOTAL_CAPACITY)){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.PARKING_FULL);
            return er;
        }
        //Comprobar que la reserva existe
        Map<String, Object> attrMapBooking = Map.of(BookingDao.BOOKINGID, attrMap.get(ParkingHistoryDao.ID_BOOKING));
        EntityResult erBooking = this.daoHelper.query(bookingDao, attrMapBooking, List.of(BookingDao.BOOKINGID, BookingDao.STARTDATE, BookingDao.ENDDATE, BookingDao.CHECKIN_DATETIME, BookingDao.CHECKOUT_DATETIME,BookingDao.ROOMID));
        if(erBooking.calculateRecordNumber() == 0){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        //Pillo el id de la habitacion y el id del hotel del parking, para comprobar que la habitacion de la reserva esta en el mismo hotel del parking
        Map<String, Object> attrMapRoom = Map.of(RoomDao.IDHABITACION, erBooking.getRecordValues(0).get(BookingDao.ROOMID),RoomDao.IDHOTEL,erParking.getRecordValues(0).get(ParkingDao.ID_HOTEL));
        EntityResult erRoom = this.daoHelper.query(roomDao, attrMapRoom, List.of(BookingDao.ROOMID,RoomDao.IDHOTEL));
        if(erRoom.calculateRecordNumber() == 0){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_SAME_HOTEL_AS_PARKING);
            return er;
        }
        //Comprobar que la fecha actual es igual o superior a la fecha de inicio de la reserva
        Date startDate = (Date) erBooking.getRecordValues(0).get(BookingDao.STARTDATE);
        Date currentDate = new Date();
        if(startDate.compareTo(currentDate) >= 0){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_STARTED);
            return er;
        }
        //TODO preguntar cesar si es correctro comprobar si hizo checkin a la hora de entrar al parking
        //Comprobar que la reserva esta activa,que hizo checkin y que no ha hecho checkout
        if(erBooking.getRecordValues(0).get(BookingDao.CHECKIN_DATETIME) == null){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_CHECKED_IN);
            return er;
        }
        if(erBooking.getRecordValues(0).get(BookingDao.CHECKOUT_DATETIME) != null){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_ALREADY_CHECKED_OUT);
            return er;
        }

        //Comprobar que la reserva no ha entrado ya en el parking (es decir tiene fecha de entrada pero no de salida)
        //Obtener todos los parking_history de la reserva
        Map<String, Object> attrMapParkingHistory = new HashMap<>();
        attrMapParkingHistory.put(ParkingHistoryDao.ID_BOOKING,attrMap.get(ParkingHistoryDao.ID_BOOKING));
        BasicField field = new BasicField(ParkingHistoryDao.EXIT_DATE);
        BasicExpression bexp = new BasicExpression(field,BasicOperator.NULL_OP,null);
        attrMapParkingHistory.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,bexp);
        EntityResult erParkingHistory = this.daoHelper.query(parkingHistoryDao, attrMapParkingHistory, List.of(ParkingHistoryDao.ID_BOOKING,ParkingHistoryDao.ENTRY_DATE,ParkingHistoryDao.EXIT_DATE));
        //Comprobar que no hay ningun parking_history que tenga fecha de entrada pero no de salida
        if(erParkingHistory.calculateRecordNumber() > 0){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_ALREADY_IN_PARKING);
            return er;
        }
        //Insertar en la tabla parking_history
        Map<String,Object> attrMapParkingHistoryInsert = Map.of(ParkingHistoryDao.ID_PARKING,attrMap.get(ParkingHistoryDao.ID_PARKING),ParkingHistoryDao.ID_BOOKING,attrMap.get(ParkingHistoryDao.ID_BOOKING),ParkingHistoryDao.ENTRY_DATE,currentDate);
        EntityResult erInsert = parkingHistoryService.parkingHistoryEnter(attrMapParkingHistoryInsert);
        //Actualizar tabla parking para sumar 1 a los coches que hay en el parking
        occupiedCapacity++;
        Map<String,Object> keyMapParkingUpdate = Map.of(ParkingDao.ID_PARKING,attrMap.get(ParkingDao.ID_PARKING));
        Map<String,Object> attrMapParkingUpdate = Map.of(ParkingDao.OCCUPIED_CAPACITY,occupiedCapacity);
        EntityResult erUpdate = this.daoHelper.update(parkingDao,attrMapParkingUpdate,keyMapParkingUpdate);
        return erUpdate;
    }

    //@Override
    public EntityResult parkingExit(Map<?, ?> attrMap) {
        EntityResult er = new EntityResultMapImpl();
        //Comprobar que me llega los datos necesarios para hacer la entrada id_booking id_parking
        if(attrMap.get(ParkingHistoryDao.ID_BOOKING) == null || attrMap.get(ParkingDao.ID_PARKING) == null){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Comprobar existe parking indicado
        Map<String, Object> attrMapParking = Map.of(ParkingDao.ID_PARKING, attrMap.get(ParkingDao.ID_PARKING));
        EntityResult erParking = this.daoHelper.query(parkingDao, attrMapParking, List.of(ParkingDao.ID_HOTEL,ParkingDao.OCCUPIED_CAPACITY));
        if(erParking.calculateRecordNumber() == 0){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.PARKING_NOT_FOUND);
            return er;
        }
        //Comprobar que la reserva existe
        Map<String, Object> attrMapBooking = Map.of(BookingDao.BOOKINGID, attrMap.get(ParkingHistoryDao.ID_BOOKING));
        EntityResult erBooking = this.daoHelper.query(bookingDao, attrMapBooking, List.of(BookingDao.BOOKINGID, BookingDao.STARTDATE, BookingDao.ENDDATE, BookingDao.CHECKIN_DATETIME, BookingDao.CHECKOUT_DATETIME,BookingDao.ROOMID));
        if(erBooking.calculateRecordNumber() == 0){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        //Comprobar que la reserva ha entrado pero no salio (es decir tiene fecha de entrada pero no de salida)
        //Obtener todos los parking_history de la reserva
        Map<String, Object> attrMapParkingHistory = new HashMap<>();
        attrMapParkingHistory.put(ParkingHistoryDao.ID_BOOKING,attrMap.get(ParkingHistoryDao.ID_BOOKING));
        attrMapParkingHistory.put(ParkingHistoryDao.ID_PARKING,attrMap.get(ParkingHistoryDao.ID_PARKING));
        BasicField field = new BasicField(ParkingHistoryDao.EXIT_DATE);
        BasicExpression bexp = new BasicExpression(field,BasicOperator.NULL_OP,null);
        attrMapParkingHistory.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,bexp);
        EntityResult erParkingHistory = this.daoHelper.query(parkingHistoryDao, attrMapParkingHistory, List.of(ParkingHistoryDao.ID,ParkingHistoryDao.ENTRY_DATE,ParkingHistoryDao.EXIT_DATE));
        //Compruebo que hay 1 y solo 1 parking_history que tenga fecha de entrada pero no de salida
        if(erParkingHistory.calculateRecordNumber() != 1){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_IN_PARKING);
            return er;
        }
        //Actualizar tabla parking_history
        int idParkingHistory = (int) erParkingHistory.getRecordValues(0).get(ParkingHistoryDao.ID);
        Map<String,Object> keyMapParkingHistoryUpdate = Map.of(ParkingHistoryDao.ID,idParkingHistory);
        Date currentDate = new Date();
        Map<String,Object> attrMapParkingHistoryUpdate = Map.of(ParkingHistoryDao.EXIT_DATE,currentDate);
        EntityResult erUpdate = parkingHistoryService.parkingHistoryExit(attrMapParkingHistoryUpdate,keyMapParkingHistoryUpdate);
        //Actualizar tabla parking para restar 1 a los coches que hay en el parking
        int occupiedCapacity = (int) erParking.getRecordValues(0).get(ParkingDao.OCCUPIED_CAPACITY);
        occupiedCapacity--;
        Map<String,Object> keyMapParkingUpdate = Map.of(ParkingDao.ID_PARKING,attrMap.get(ParkingDao.ID_PARKING));
        Map<String,Object> attrMapParkingUpdate = Map.of(ParkingDao.OCCUPIED_CAPACITY,occupiedCapacity);
        EntityResult erUpdateParking = this.daoHelper.update(parkingDao,attrMapParkingUpdate,keyMapParkingUpdate);
        //Lo calculo al final, es mas sencillo entender que si la fecha de salida existe 1o mas veces ya se tuvo en cuenta el dia de hoy
        //De la otra forma tendria que buscar los que tuvieran fecha de salida hoy,
        calculateParkingTime(attrMap);
        return er;
    }
    public EntityResult calculateParkingTime(Map<?,?> attr){
        Map<String,Object> attrParkingHistory = new HashMap<>();
        BasicField fieldSalida = new BasicField(ParkingHistoryDao.EXIT_DATE);
        //Tengo que buscar que no haya salido del parking ese mismo dia,si salio ese dia ya se conto
        BasicExpression bexp1 = new BasicExpression(fieldSalida,BasicOperator.EQUAL_OP,new Date());//FechaSalida=Hoy
        BasicExpression bexp2 = new BasicExpression(fieldSalida,BasicOperator.NULL_OP,null);//FechaSalida=null
        BasicExpression bexp = new BasicExpression(bexp1,BasicOperator.OR_OP,bexp2);//FechaSalida=Hoy OR FechaSalida=null
        //Añado la condicion de que sea la reserva indicada
        attrParkingHistory.put(ParkingHistoryDao.ID_BOOKING,attr.get(ParkingHistoryDao.ID_BOOKING));
        //Añado la condicion de que la fecha de salida sea hoy o null
        attrParkingHistory.put(ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY,bexp);

        EntityResult erParkingHistory = this.daoHelper.query(parkingHistoryDao,attrParkingHistory,List.of(ParkingHistoryDao.ID,ParkingHistoryDao.ID_BOOKING,ParkingHistoryDao.ENTRY_DATE,ParkingHistoryDao.EXIT_DATE));
        //Si hay un registro de salida + 1 nulo,es decir que entro y salio el mismo dia,devuelvo el registro
        if(erParkingHistory.calculateRecordNumber() > 1){
            return erParkingHistory;
        }
        //Si no hay registro de salida hoy,obtengo la fecha de entrada y calculo el tiempo que ha estado en el parking
        Date entrada = (Date) erParkingHistory.getRecordValues(0).get(ParkingHistoryDao.ENTRY_DATE);
        Date salida = (Date) erParkingHistory.getRecordValues(0).get(ParkingHistoryDao.EXIT_DATE);
        //Calculo los dias que pasaron entre la entrada y la salida
        long diff = salida.getTime() - entrada.getTime();
        long diffDays = diff / (24 * 60 * 60 * 1000);
        //Si el tiempo que ha estado en el parking es 0 dias,es decir entro y salio el mismo dia,le sumo 1 dia
        if(diffDays == 0){
            diffDays++;
        }
        //Obtengo el id de la reserva
        int idReserva = (int) attr.get(ParkingHistoryDao.ID_BOOKING);
        //Obtengo el id del parking
        int idParking = (int) attr.get(ParkingDao.ID_PARKING);
        //Obtengo el precio del parking
        Map<String,Object> attrMapParking = Map.of(ParkingDao.ID_PARKING,idParking);
        EntityResult erParking = this.daoHelper.query(parkingDao,attrMapParking,List.of(ParkingDao.PRICE,ParkingDao.DESCRIPTION));
        BigDecimal price = (BigDecimal) erParking.getRecordValues(0).get(ParkingDao.PRICE);
        //Calculo el precio total
        BigDecimal totalPrice = price.multiply(new BigDecimal(diffDays));
        //Añado el precio a la tabla extra_expenses
        StringBuilder sb = new StringBuilder();
        String description = (String) erParking.getRecordValues(0).get(ParkingDao.DESCRIPTION);
        sb.append(description);
        sb.append(" ");
        sb.append(entrada+"/"+salida);
        Map<String,Object> attrMapExtraExpenses = new HashMap<>();
        attrMapExtraExpenses.put(ExtraExpensesDao.BOOKINGID,idReserva);
        attrMapExtraExpenses.put(ExtraExpensesDao.CONCEPT,sb.toString());
        attrMapExtraExpenses.put(ExtraExpensesDao.PRICE,totalPrice);
        EntityResult erExtraExpenses = extraExpensesService.extraexpensesInsert(attrMapExtraExpenses);
        return erExtraExpenses;
    }

    //@Override
    public EntityResult parkingInsert(Map<String, Object> attrMap) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        //Probamos que se mande todos los datos neccesarios
        if (attrMap.get(ParkingDao.PRICE) == null || attrMap.get(ParkingDao.ID_HOTEL) == null || attrMap.get(ParkingDao.TOTAL_CAPACITY) == null || attrMap.get(ParkingDao.DESCRIPTION) == null) {
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Existe el hotel
        Map<String, Object> hotelid = new HashMap<>();
        hotelid.put(HotelDao.ID, attrMap.get(ParkingDao.ID_HOTEL));
        EntityResult hotel = this.daoHelper.query(this.hotelDao, hotelid, List.of(HotelDao.ID));
        if (hotel.calculateRecordNumber() == 0) {
            er.setMessage(ErrorMessages.HOTEL_NOT_EXIST);
            return er;
        }
        //Se comprueba que se mande una capacidad que sea un numero positivo
        try {
            if (Integer.parseInt((String) attrMap.get(ParkingDao.TOTAL_CAPACITY)) > 0){
                er.setMessage(ErrorMessages.CAPACITY_NOT_POSITIVE);
                return er;
            }
        } catch (NumberFormatException e) {
            er.setMessage(ErrorMessages.CAPACITY_NOT_NUMBER);
            return er;
        }
        return this.daoHelper.insert(this.parkingDao, attrMap);
    }

    @Override
    public EntityResult parkingUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        if (keyMap.get(ParkingDao.ID_PARKING) == null) {
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Comprobar si existe el parking
        EntityResult parking = this.daoHelper.query(this.parkingDao, keyMap, List.of(ParkingDao.ID_HOTEL));
        if (parking.calculateRecordNumber() == 0) {
            er.setMessage(ErrorMessages.PARKING_NOT_FOUND);
            return er;
        }
        //Comprobar que la capacidad total es un numero positivo
        if (keyMap.containsKey(ParkingDao.TOTAL_CAPACITY)) {
            try {
                if (Integer.parseInt((String) attrMap.get(ParkingDao.TOTAL_CAPACITY)) > 0){
                    er.setMessage(ErrorMessages.CAPACITY_NOT_POSITIVE);
                    return er;
                }
            } catch (NumberFormatException e) {
                er.setMessage(ErrorMessages.CAPACITY_NOT_NUMBER);
                return er;
            }
        }
        return this.daoHelper.update(this.parkingDao, attrMap, keyMap);
    }

    @Override
    public EntityResult parkingDelete(Map<String, Object> keyMap) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        //Comprobar que se manda la key
        if (keyMap.get(ParkingDao.ID_PARKING) == null) {
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Comprobar que existe el parking
        EntityResult parking = this.daoHelper.query(this.parkingDao, keyMap, List.of(ParkingDao.ID_HOTEL));
        if (parking.calculateRecordNumber() == 0) {
            er.setMessage(ErrorMessages.PARKING_NOT_FOUND);
            return er;
        }
        return this.daoHelper.delete(this.parkingDao, keyMap);
    }


}
