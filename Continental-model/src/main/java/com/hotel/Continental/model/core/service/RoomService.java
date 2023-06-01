package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IRoomService;
import com.hotel.Continental.model.core.dao.BookDao;
import com.hotel.Continental.model.core.dao.HotelDao;
import com.hotel.Continental.model.core.dao.RoomDao;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicField;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
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
    private BookDao bookDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    public EntityResult roomQuery(Map<?, ?> keyMap, List<?> attrList) {
        EntityResult room= this.daoHelper.query(this.roomDao, keyMap, attrList);
        if(room.calculateRecordNumber() == 0){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("La habitación no existe");
            return er;
        }
        return this.daoHelper.query(roomDao, keyMap, attrList);
    }

    public EntityResult roomInsert(Map<?, ?> attrMap) {
        return this.daoHelper.insert(roomDao, attrMap);
    }

    public EntityResult roomDelete(Map<?, ?> keyMap) {
        //si la habitacion no existe lanzar un error
        EntityResult room = roomQuery(keyMap, new ArrayList<>());
        if (room.getCode()==EntityResult.OPERATION_WRONG){
            return room;
        }
        //Si la habitacion existe y esta dada de baja lanzar un error
        if (room.getRecordValues(0).get(RoomDao.ROOMDOWNDATE) != null) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("La habitación ya está dada de baja");
            return er;
        }
        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put(RoomDao.ROOMDOWNDATE, new Timestamp(System.currentTimeMillis()));
        EntityResult er = this.daoHelper.update(this.roomDao, attrMap, keyMap);
        er.setMessage("La habitación ha sido dada de baja con fecha "+new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return er;
    }

    public EntityResult roomUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        //si la habitacion no existe lanzar un error
        EntityResult room = roomQuery(keyMap, new ArrayList<>());
        if (room.getCode()==EntityResult.OPERATION_WRONG){
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
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            initialDate = formatter.parse(initialDateString);
            finalDate = formatter.parse(finalDateString);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        BasicField startDateField = new BasicField(BookDao.STARTDATE);
        BasicField endDateField = new BasicField(BookDao.ENDDATE);
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
        attrIdsBookedRooms.add(BookDao.ROOMID);

        EntityResult bookedRooms = this.daoHelper.query(this.bookDao, filter, attrIdsBookedRooms, BookDao.QUERY_BOOKED_ROOMS);
        //Si no hay habitaciones reservadas para esas fechas, se obtienen todas las habitaciones
        if (bookedRooms.get(BookDao.ROOMID) == null) {
            return this.daoHelper.query(this.roomDao, keyMap, attrList);
        }
        //Si hay habitaciones reservadas para esas fechas, se obtienen las habitaciones que no estén en la lista de habitaciones reservadas
        Map<String, Object> filter2 = new HashMap<>();
        //Se filtran por las habitaciones reservadas
        BasicExpression bexpByIDS = new BasicExpression(new BasicField(RoomDao.IDHABITACION), BasicOperator.NOT_IN_OP, bookedRooms.get(BookDao.ROOMID));

        //Si se especifica un id de hotel, se obtienen las habitaciones de ese hotel
        if (keyMap.get(RoomDao.IDHOTEL) != null) {

            BasicExpression bexpByHotel = new BasicExpression(new BasicField(RoomDao.IDHOTEL), BasicOperator.EQUAL_OP, keyMap.get(RoomDao.IDHOTEL));
            BasicExpression bexpByIDSandHotel = new BasicExpression(bexpByIDS, BasicOperator.AND_OP, bexpByHotel);
            //Se usa como filtro los ids+hotel
            filter2.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexpByIDSandHotel);
        }else{
            //Se usa como filtro los ids
            filter2.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexpByIDS);
        }
        return this.daoHelper.query(this.roomDao, filter2, attrList);

    }

}
