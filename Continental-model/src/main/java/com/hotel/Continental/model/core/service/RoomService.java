package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IRoomService;
import com.hotel.Continental.model.core.dao.BookDao;
import com.hotel.Continental.model.core.dao.RoomDao;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicField;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicOperator;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.ontimize.jee.common.dto.EntityResult;
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
        return this.daoHelper.query(roomDao, keyMap, attrList);
    }

    public EntityResult roomInsert(Map<?, ?> attrMap) {
        return this.daoHelper.insert(roomDao, attrMap);
    }

    public EntityResult roomDelete(Map<?, ?> keyMap) {
        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put("roomdowndate", new Timestamp(Calendar.getInstance().getTimeInMillis()));
        return this.daoHelper.update(this.roomDao, attrMap, keyMap);
    }

    @Override
    public EntityResult freeRoomsQuery(Map<String, Object> keyMap, List<String> attrList) throws OntimizeJEERuntimeException {
//        //ColumnsRooms es una lista de columnas
//        List<String> columnsRooms = new ArrayList<>();
//        columnsRooms.add(RoomDao.IDHABITACION);
//
//        String startDateString = keyMap.get("FECHAINICIO").toString();
//        String endDateString = keyMap.get("FECHAFIN").toString();
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//
//        java.util.Date startdate = null;
//        java.util.Date endDate = null;
//
//        try {
//            startdate = formatter.parse(startDateString);
//            endDate = formatter.parse(endDateString);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        Date sqlStartDate = new Date(startdate.getTime());
//        Date sqlEndDate = new Date(endDate.getTime());
////        //Necesito que la fecha de inicio no este entre la fecha fin y la fecha inicio de la reserva
////        BasicExpression subquery = new BasicExpression(new SQLStatementBuilder.BasicField(BookDao.STARTDATE), SQLStatementBuilder.BasicOperator.NOT_IN_OP, Arrays.asList(sqlStartDate, sqlEndDate));
////        //Necesito que la fecha de fin no este entre la fecha fin y la fecha inicio de la reserva
////        BasicExpression subquery2 = new BasicExpression(new SQLStatementBuilder.BasicField(BookDao.ENDDATE), SQLStatementBuilder.BasicOperator.NOT_IN_OP, Arrays.asList(sqlStartDate, sqlEndDate));
////        //Ahora junto las dos condiciones con un AND
////        BasicExpression bexp = new BasicExpression(subquery, SQLStatementBuilder.BasicOperator.AND_OP, subquery2);
////        Map<String, Object> filter = new HashMap<>();
////        filter.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexp);
//
//        //RoomsId son los id de las rooms ocupadas
//        EntityResult roomsId = this.daoHelper.query(this.roomDao, filter, columnsRooms, RoomDao.QUERY_FREE_ROOMS);
//        List<Integer> ids = (List<Integer>) roomsId.get(RoomDao.IDHABITACION);
//        //idRoomsToExclude son los valores a usar en el NOT IN
//        Map<String, Object> idRoomsToExclude = new HashMap<>();
//        BasicExpression bexp1 = new BasicExpression(new SQLStatementBuilder.BasicField(RoomDao.IDHABITACION), SQLStatementBuilder.BasicOperator.IN_OP, ids);
//        idRoomsToExclude.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexp1);
//        EntityResult rooms = this.roomQuery(idRoomsToExclude, attrList);
//        return rooms;

        String initialDateString = keyMap.remove("INITIALDATE").toString();
        String finalDateString = keyMap.remove("FINALDATE").toString();
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
        BasicExpression bexp2=new BasicExpression(endDateField, BasicOperator.MORE_EQUAL_OP, finalDate);//and r.fechaFin >= ? 2)
        BasicExpression bexp3 = new BasicExpression(startDateField, BasicOperator.MORE_EQUAL_OP, initialDate);
        BasicExpression bexp4 = new BasicExpression(startDateField, BasicOperator.LESS_EQUAL_OP, finalDate);
        BasicExpression bexp3_4=new BasicExpression(bexp3, BasicOperator.AND_OP, bexp4);//or r.fechaInicio between ? 1 and ? 2

        BasicExpression bexp5 = new BasicExpression(endDateField, BasicOperator.MORE_EQUAL_OP, initialDate);
        BasicExpression bexp6 = new BasicExpression(endDateField, BasicOperator.LESS_EQUAL_OP, initialDate);
        BasicExpression bexp5_6=new BasicExpression(bexp5, BasicOperator.AND_OP, bexp6);//or r.fechaFin between ? 1 and ? 2)")

        BasicExpression bexp1_2=new BasicExpression(bexp1, BasicOperator.AND_OP, bexp2);
        BasicExpression bexp3_4_5_6=new BasicExpression(bexp3_4, BasicOperator.OR_OP, bexp5_6);
        BasicExpression bexp=new BasicExpression(bexp1_2, BasicOperator.OR_OP, bexp3_4_5_6);

        Map<String, Object> filter = new HashMap<>();
        filter.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexp);
        EntityResult bookedRooms=this.daoHelper.query(this.bookDao, filter, attrList, BookDao.QUERY_BOOKED_ROOMS);

        return  bookedRooms;
    }

}
