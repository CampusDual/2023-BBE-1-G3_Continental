package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRoomService;
import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicField;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Lazy
@Service("RoomService")
public class RoomService implements IRoomService {

    @Autowired
    private RoomDao roomDao;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    //declara un a constante con yyyy-MM-dd
    private static final String DATE_FORMAT = "yyyy-MM-dd";

    /**
     * Metodo que devuelve un EntityResult con los datos de la habitacion
     *
     * @param keyMap   Mapa de claves que identifican la habitacion
     * @param attrList Lista de atributos que se quieren obtener
     * @return EntityResult con los datos de la habitacion o un mensaje de error
     */
    public EntityResult roomQuery(Map<?, ?> keyMap, List<?> attrList) {
        EntityResult room = this.daoHelper.query(this.roomDao, keyMap, attrList);
        if (room.calculateRecordNumber() == 0) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ROOM_NOT_EXIST);
            return er;
        }
        return this.daoHelper.query(roomDao, keyMap, attrList);
    }

    /**
     * Método inserta una habitacion en la base de datos y devuelve un EntityResult con los datos de la habitacion
     *
     * @param attrMap Mapa de atributos de la habitacion
     * @return EntityResult con los datos de la habitacion o un mensaje de error
     */
    public EntityResult roomInsert(Map<?, ?> attrMap) {
        //Comprobar que se envian los campos necesarios
        if (!attrMap.containsKey(RoomDao.IDHOTEL) || !attrMap.containsKey(RoomDao.ROOMNUMBER)) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Comprobar que el hotel existe
        EntityResult hotel = this.daoHelper.query(this.roomDao, new HashMap<>(), Arrays.asList(RoomDao.IDHOTEL));
        if (hotel.calculateRecordNumber() == 0) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.HOTEL_NOT_EXIST);
            return er;
        }
        //Comprobar que la habitacion no existe
        Map<Object, Object> erQueryRoomKeyMap = new HashMap<>();
        erQueryRoomKeyMap.put(RoomDao.ROOMNUMBER, attrMap.get(RoomDao.ROOMNUMBER));
        erQueryRoomKeyMap.put(RoomDao.IDHOTEL, attrMap.get(RoomDao.IDHOTEL));
        EntityResult erQueryRoom = this.daoHelper.query(this.roomDao, erQueryRoomKeyMap, Arrays.asList(RoomDao.ROOMNUMBER, RoomDao.IDHOTEL));
        if (erQueryRoom.calculateRecordNumber() != 0) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ROOM_ALREADY_EXIST);
            return er;
        }
        EntityResult erInsertRoom = this.daoHelper.insert(this.roomDao, attrMap);
        erInsertRoom.setMessage("La habitación ha sido dada de alta con fecha " + new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        return erInsertRoom;
    }

    /**
     * Método que da de baja una habitacion y devuelve un EntityResult con los datos de la habitacion
     *
     * @param keyMap Mapa de claves que identifican la habitacion
     * @return EntityResult con los datos de la habitacion o un mensaje de error
     */
    public EntityResult roomDelete(Map<?, ?> keyMap) {
        //si la habitacion no existe lanzar un error
        EntityResult room = roomQuery(keyMap, Arrays.asList(RoomDao.IDHABITACION, RoomDao.ROOMDOWNDATE));
        if (room.getCode() == EntityResult.OPERATION_WRONG) {
            return room;
        }
        //Si la habitacion existe y esta dada de baja lanzar un error
        if (room.getRecordValues(0).get(RoomDao.ROOMDOWNDATE) != null) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.ROOM_ALREADY_INACTIVE);
            return er;
        }
        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put(RoomDao.ROOMDOWNDATE, new Timestamp(System.currentTimeMillis()));
        EntityResult er = this.daoHelper.update(this.roomDao, attrMap, keyMap);
        er.setMessage("La habitación ha sido dada de baja con fecha " + new SimpleDateFormat(DATE_FORMAT).format(new Date()));
        return er;
    }

    /**
     * Método que actualiza los datos de una habitacion y devuelve un EntityResult con los datos de la habitacion
     *
     * @param attrMap Mapa de atributos de la habitacion
     * @param keyMap  Mapa de claves que identifican la habitacion
     * @return EntityResult con los datos de la habitacion o un mensaje de error
     */
    public EntityResult roomUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        List<String> columns = new ArrayList<>();
        columns.add("idhabitacion");
        //si la habitacion no existe lanzar un error
        EntityResult room = roomQuery(keyMap, columns);
        if (room.getCode() == EntityResult.OPERATION_WRONG) {
            return room;
        }
        return this.daoHelper.update(this.roomDao, attrMap, keyMap);
    }

    @Override
    public EntityResult freeRoomsQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {
        String initialDateString = keyMap.remove("initialdate").toString();
        String finalDateString = keyMap.remove("finaldate").toString();
        Date initialDate = null;
        Date finalDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        try {
            initialDate = formatter.parse(initialDateString);
            finalDate = formatter.parse(finalDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        BasicField startDateField = new BasicField(BookingDao.STARTDATE);
        BasicField endDateField = new BasicField(BookingDao.ENDDATE);
        BasicExpression bexp1 = new BasicExpression(startDateField, BasicOperator.LESS_EQUAL_OP, initialDate);//(r.fechaInicio <= ? 1
        BasicExpression bexp2 = new BasicExpression(endDateField, BasicOperator.MORE_EQUAL_OP, finalDate);//and r.fechaFin >= ? 2)
        BasicExpression bexp3 = new BasicExpression(startDateField, BasicOperator.MORE_EQUAL_OP, initialDate);
        BasicExpression bexp4 = new BasicExpression(startDateField, BasicOperator.LESS_EQUAL_OP, finalDate);
        BasicExpression bexp3and4 = new BasicExpression(bexp3, BasicOperator.AND_OP, bexp4);//or r.fechaInicio between ? 1 and ? 2

        BasicExpression bexp5 = new BasicExpression(endDateField, BasicOperator.MORE_EQUAL_OP, initialDate);
        BasicExpression bexp6 = new BasicExpression(endDateField, BasicOperator.LESS_EQUAL_OP, initialDate);
        BasicExpression bexp5and6 = new BasicExpression(bexp5, BasicOperator.AND_OP, bexp6);//or r.fechaFin between ? 1 and ? 2)")

        BasicExpression bexp1and2 = new BasicExpression(bexp1, BasicOperator.AND_OP, bexp2);
        BasicExpression bexp3to6 = new BasicExpression(bexp3and4, BasicOperator.OR_OP, bexp5and6);
        BasicExpression bexp = new BasicExpression(bexp1and2, BasicOperator.OR_OP, bexp3to6);//(r.fechaInicio <= ? 1 and r.fechaFin >= ? 2) or r.fechaInicio between ? 1 and ? 2 or r.fechaFin between ? 1 and ? 2

        Map<String, Object> filter = new HashMap<>();
        filter.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexp);
        List<String> attrIdsBookedRooms = new ArrayList<>();
        attrIdsBookedRooms.add(BookingDao.ROOMID);

        EntityResult bookedRooms = this.daoHelper.query(this.bookingDao, filter, attrIdsBookedRooms, BookingDao.QUERY_BOOKED_ROOMS);
        //Si no hay habitaciones reservadas para esas fechas, se obtienen todas las habitaciones
        if (bookedRooms.get(BookingDao.ROOMID) == null) {
            return this.daoHelper.query(this.roomDao, keyMap, attrList);
        }
        //Si hay habitaciones reservadas para esas fechas, se obtienen las habitaciones que no estén en la lista de habitaciones reservadas
        Map<String, Object> filter2 = new HashMap<>();
        //Se filtran por las habitaciones reservadas
        BasicExpression bexpByIDS = new BasicExpression(new BasicField(RoomDao.IDHABITACION), BasicOperator.NOT_IN_OP, bookedRooms.get(BookingDao.ROOMID));

        //Si se especifica un id de hotel, se obtienen las habitaciones de ese hotel
        if (keyMap.get(RoomDao.IDHOTEL) != null) {

            BasicExpression bexpByHotel = new BasicExpression(new BasicField(RoomDao.IDHOTEL), BasicOperator.EQUAL_OP, keyMap.get(RoomDao.IDHOTEL));
            BasicExpression bexpByIDSandHotel = new BasicExpression(bexpByIDS, BasicOperator.AND_OP, bexpByHotel);
            //Se usa como filtro los ids+hotel
            filter2.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexpByIDSandHotel);
        } else {
            //Se usa como filtro los ids
            filter2.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexpByIDS);
        }
        return this.daoHelper.query(this.roomDao, filter2, attrList);

    }

}
