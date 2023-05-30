package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IHotelService;
import com.hotel.Continental.api.core.service.IRoomService;
import com.hotel.Continental.model.core.dao.HotelDao;
import com.ontimize.jee.common.db.SQLStatementBuilder.BasicExpression;
import com.hotel.Continental.model.core.dao.RoomDao;
import com.ontimize.jee.common.db.SQLStatementBuilder;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.exceptions.OntimizeJEERuntimeException;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;

@Lazy
@Service("RoomService")
public class RoomService implements IRoomService {

    @Autowired
    private RoomDao roomDao;

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
        //ColumnsRooms es una lista de columnas
        List<String> columnsRooms = new ArrayList<>();
        columnsRooms.add(RoomDao.IDHABITACION);

        Calendar cal = Calendar.getInstance();
      //  cal.set(year, 0, 1);
        Date startDate = cal.getTime();
       // cal.set(year + 1, 0, 1);


        //RoomsId son los id de las free rooms
        EntityResult roomsId = this.daoHelper.query(this.roomDao, keyMap, columnsRooms, RoomDao.QUERY_FREE_ROOMS);
        List<Integer> ids = (List<Integer>) roomsId.get(RoomDao.IDHABITACION);

        //idRoomsToExclude son los valores a usar en el NOT IN
        Map<String, Object> idRoomsToExclude = new HashMap<>();
        BasicExpression bexp1 = new BasicExpression(new SQLStatementBuilder.BasicField(RoomDao.IDHABITACION), SQLStatementBuilder.BasicOperator.NOT_IN_OP, ids);
        idRoomsToExclude.put(SQLStatementBuilder.ExtendedSQLConditionValuesProcessor.EXPRESSION_KEY, bexp1);
        EntityResult rooms = this.roomQuery(idRoomsToExclude, attrList);
        return rooms;
    }

}
