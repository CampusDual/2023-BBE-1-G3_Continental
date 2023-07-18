package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRoomService;
import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.HotelDao;
import com.hotel.continental.model.core.dao.RoomDao;
import com.hotel.continental.model.core.dao.RoomTypeDao;
import com.hotel.continental.model.core.tools.Messages;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicField;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Lazy
@Service("RoomService")
public class RoomService implements IRoomService {

    @Autowired
    private HotelDao hotelDao;
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private BookingDao bookingDao;
    @Autowired
    private RoomTypeDao roomTypeDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    //declara un a constante con yyyy-MM-dd
    private static final String DATE_FORMAT = "yyyy-MM-dd";
    private static final String INITIALDATE = "initialdate";
    private static final String FINALDATE = "finaldate";

    /**
     * Metodo que devuelve un EntityResult con los datos de la habitacion
     *
     * @param keyMap   Mapa de claves que identifican la habitacion
     * @param attrList Lista de atributos que se quieren obtener
     * @return EntityResult con los datos de la habitacion o un mensaje de error
     */
    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult roomQuery(Map<?, ?> keyMap, List<?> attrList) {
        EntityResult er;
        er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_WRONG);

        if(attrList.isEmpty()) {
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }

        EntityResult room = this.daoHelper.query(this.roomDao, keyMap, attrList);
        if (room.calculateRecordNumber() == 0) {
            er.setMessage(Messages.ROOM_NOT_EXIST);
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
    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult roomInsert(Map<?, ?> attrMap) {
        //Comprobar que se envian los campos necesarios
        if (!attrMap.containsKey(RoomDao.HOTEL_ID) || !attrMap.containsKey(RoomDao.ROOM_NUMBER) || !attrMap.containsKey(RoomDao.ROOM_TYPE_ID)) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }
        //Comprobar que el hotel existe
        Map<String, Object> filterHotel = new HashMap<>();
        filterHotel.put(HotelDao.HOTEL_ID, attrMap.get(RoomDao.HOTEL_ID));
        EntityResult hotel = this.daoHelper.query(this.hotelDao, filterHotel, Arrays.asList(HotelDao.HOTEL_ID));

        if (hotel == null || hotel.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.HOTEL_NOT_EXIST);
            return er;
        }

        Map<String, Object> filterType = new HashMap<>();
        filterType.put(RoomTypeDao.TYPE_ID, attrMap.get(RoomDao.ROOM_TYPE_ID));
        EntityResult types = this.daoHelper.query(this.roomTypeDao, filterType, List.of(RoomTypeDao.TYPE));
        if (types.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.TYPE_NOT_EXISTENT);
            return er;
        }

        //Comprobar que la habitacion no existe
        Map<Object, Object> erQueryRoomKeyMap = new HashMap<>();
        erQueryRoomKeyMap.put(RoomDao.ROOM_NUMBER, attrMap.get(RoomDao.ROOM_NUMBER));
        erQueryRoomKeyMap.put(RoomDao.HOTEL_ID, attrMap.get(RoomDao.HOTEL_ID));
        EntityResult erQueryRoom = this.daoHelper.query(this.roomDao, erQueryRoomKeyMap, Arrays.asList(RoomDao.ROOM_NUMBER, RoomDao.HOTEL_ID));

        if (erQueryRoom.calculateRecordNumber() != 0) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.ROOM_ALREADY_EXIST);
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
    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult roomDelete(Map<?, ?> keyMap) {
        //comprobar que se envian los campos necesarios
        if (!keyMap.containsKey(RoomDao.ROOM_ID)) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }
        //si la habitacion no existe lanzar un error
        EntityResult room = roomQuery(keyMap, Arrays.asList(RoomDao.ROOM_ID, RoomDao.ROOM_DOWN_DATE));
        if (room.getCode() == EntityResult.OPERATION_WRONG) {
            room.setMessage(Messages.ROOM_NOT_EXIST);
            return room;
        }
        //Si la habitacion existe y esta dada de baja lanzar un error
        if (room.getRecordValues(0).get(RoomDao.ROOM_DOWN_DATE) != null) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.ROOM_ALREADY_INACTIVE);
            return er;
        }
        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put(RoomDao.ROOM_DOWN_DATE, new Timestamp(System.currentTimeMillis()));
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
    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult roomUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        //comprobar que se envian los campos necesarios
        if (!keyMap.containsKey(RoomDao.ROOM_ID)) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }
        if(attrMap.isEmpty()){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }
        if (attrMap.containsKey(RoomDao.HOTEL_ID)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(Messages.COLUMN_NOT_EDITABLE);
            return er;
        }

        if (attrMap.get(RoomDao.ROOM_TYPE_ID) != null) {
            Map<String, Object> filterType = new HashMap<>();
            filterType.put(RoomTypeDao.TYPE_ID, attrMap.get(RoomDao.ROOM_TYPE_ID));
            EntityResult types = this.daoHelper.query(this.roomTypeDao, filterType, List.of(RoomTypeDao.TYPE));
            if (types.calculateRecordNumber() == 0) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(1);
                er.setMessage(Messages.TYPE_NOT_EXISTENT);
                return er;
            }
        }

        List<String> columns = new ArrayList<>();
        columns.add(RoomDao.ROOM_ID);
        //si la habitacion no existe lanzar un error
        EntityResult room = roomQuery(keyMap, columns);
        if (room.getCode() == EntityResult.OPERATION_WRONG) {
            room.setMessage(Messages.ROOM_NOT_EXIST);
            return room;
        }
        return this.daoHelper.update(this.roomDao, attrMap, keyMap);
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult freeRoomsQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {
        //Copiamos el mapa de claves para no modificar el original
        Map<String, Object> keyMapCopy = new HashMap<>(keyMap);
        String initialDateString = keyMapCopy.remove(INITIALDATE).toString();
        String finalDateString = keyMapCopy.remove(FINALDATE).toString();
        Date initialDate = null;
        Date finalDate = null;
        SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
        //Comprubo que el formato de las fechas es correcto
        try {
            initialDate = formatter.parse(initialDateString);
            finalDate = formatter.parse(finalDateString);
            keyMap.remove(INITIALDATE);
            keyMap.remove(FINALDATE);
            keyMap.put(INITIALDATE, initialDate);
            keyMap.put(FINALDATE, finalDate);
        } catch (ParseException e) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.DATE_FORMAT_ERROR);
            return er;
        }
        //Comprobar que la fecha inicial es anterior a la final
        if (initialDate.after(finalDate)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.FINAL_DATE_BEFORE_INITIAL_DATE);
            return er;
        }
        //Comprobar que la fecha inicial es posterior a la actual
        if (initialDate.before(new Date())) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.INITIAL_DATE_BEFORE_CURRENT_DATE);
            return er;
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
            return this.daoHelper.query(this.roomDao, keyMapCopy, attrList);
        }
        //Si hay habitaciones reservadas para esas fechas, se obtienen las habitaciones que no estén en la lista de habitaciones reservadas
        Map<String, Object> filter2 = new HashMap<>();
        //Se filtran por las habitaciones reservadas
        BasicExpression bexpByIDS = new BasicExpression(new BasicField(RoomDao.ROOM_ID), BasicOperator.NOT_IN_OP, bookedRooms.get(BookingDao.ROOMID));

        //Si se especifica un id de hotel, se obtienen las habitaciones de ese hotel
        if (keyMapCopy.get(RoomDao.HOTEL_ID) != null) {

            BasicExpression bexpByHotel = new BasicExpression(new BasicField(RoomDao.HOTEL_ID), BasicOperator.EQUAL_OP, keyMapCopy.get(RoomDao.HOTEL_ID));
            BasicExpression bexpByIDSandHotel = new BasicExpression(bexpByIDS, BasicOperator.AND_OP, bexpByHotel);
            //Se usa como filtro los ids+hotel
            filter2.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexpByIDSandHotel);
        } else {
            //Se usa como filtro los ids
            filter2.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexpByIDS);
        }
        //Si se especifica un id de tipo de habitacion, se obtienen las habitaciones de ese tipo
        if (keyMapCopy.get(RoomDao.ROOM_TYPE_ID) != null) {
            BasicExpression bexpByRoomType = new BasicExpression(new BasicField(RoomDao.ROOM_TYPE_ID), BasicOperator.EQUAL_OP, keyMapCopy.get(RoomDao.ROOM_TYPE_ID));
            BasicExpression bexpByIDSandRoomType = new BasicExpression(bexpByIDS, BasicOperator.AND_OP, bexpByRoomType);
            //Se usa como filtro los ids+tipo de habitacion
            filter2.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexpByIDSandRoomType);
        }
        return this.daoHelper.query(this.roomDao, filter2, attrList);
    }

}
