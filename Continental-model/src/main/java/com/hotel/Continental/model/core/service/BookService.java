package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IBookService;
import com.hotel.Continental.model.core.dao.BookDao;
import com.hotel.Continental.model.core.dao.RoomDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Lazy
@Service("BookService")
public class BookService implements IBookService {
    @Autowired
    private BookDao bookDao;
    @Autowired
    private RoomService roomService;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    /**
     * Metodo que devuelve las reservas
     * @param keyMap   Mapa con los campos de la clave
     * @param attrList Lista de atributos que se quieren devolver
     * @return EntityResult con las reservas o un mensaje de error
     */
    @Override
    public EntityResult bookQuery(Map<?, ?> keyMap, List<?> attrList) {
        EntityResult result = this.daoHelper.query(this.bookDao, keyMap, attrList);
        if (result.calculateRecordNumber() == 0) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("Esa reserva no existe");
            return er;
        }
        return result;
    }

    public EntityResult bookInsert(Map<String, Object> attrMap) {
        String initialDateString = attrMap.remove(BookDao.STARTDATE).toString();
        String finalDateString = attrMap.remove(BookDao.ENDDATE).toString();
        Date initialDate = getDateFromString(initialDateString);
        Date finalDate = getDateFromString(finalDateString);
        attrMap.put(BookDao.STARTDATE, initialDate);
        attrMap.put(BookDao.ENDDATE, finalDate);
        if(finalDate==null || initialDate==null){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("Problemas al parsear las fechas");
            return er;
        }
        if (finalDate.before(initialDate)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("La fecha de fin no puede ser anterior a la de inicio");
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
        if (attrMap.get(BookDao.ROOMID) != null) {
            roomAttrMap.put(BookDao.ROOMID, attrMap.get(RoomDao.IDHABITACION));
        }
        EntityResult habitacionesLibres = roomService.freeRoomsQuery(roomAttrMap, roomKeyMap);//Todas las habitaciones libres entre esas dos fechas
        //Si hay habitaciones libres se busca si esa habitacion esta libre en esas fechas
        //Si esta libre se inserta
        //Si no esta libre se devuelve un error
        if (habitacionesLibres.calculateRecordNumber() > 0) {
            //Buscamos si la habitacion esta libre en esas fechas
            Map<String, Object> room = habitacionesLibres.getRecordValues(0);
            attrMap.put(BookDao.ROOMID, room.get(RoomDao.IDHABITACION));
            return this.daoHelper.insert(this.bookDao, attrMap);
        }
        EntityResult er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_WRONG);
        er.setMessage("La habitacion no esta libre en esas fechas");
        return er;

    }

    /**
     * Metodo que borra una reserva
     * @param keyMap Mapa con los campos de la clave
     * @return EntityResult con la reserva borrada o un mensaje de error
     */
    public EntityResult bookDelete(Map<?, ?> keyMap) {
        //Primero comprobamos si la reserva existe
        EntityResult book = this.daoHelper.query(this.bookDao, keyMap, null);
        if (book.getCode() == EntityResult.OPERATION_WRONG) {
            return book;
        }
        return this.daoHelper.delete(this.bookDao, keyMap);
    }

    /**
     * Metodo que actualiza una reserva
     * @param attrMap Mapa con los campos a actualizar
     * @param keyMap Mapa con los campos de la clave
     * @return EntityResult con la reserva actualizada o un mensaje de error
     */
    @Override
    public EntityResult bookUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        //Primero comprobamos si la reserva existe
        EntityResult book = this.daoHelper.query(this.bookDao, keyMap, null);
        if (book.getCode() == EntityResult.OPERATION_WRONG) {
            return book;
        }
        //Guardamos las fechas en variables para poder compararlas
        Date initialDate = getDateFromString(attrMap.remove(BookDao.STARTDATE).toString());
        Date finalDate = getDateFromString(attrMap.remove(BookDao.ENDDATE).toString());
        if(finalDate==null || initialDate==null){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("Problemas al parsear las fechas");
            return er;
        }
        if (finalDate.before(initialDate)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("La fecha de fin no puede ser anterior a la de inicio");
            return er;
        }
        //actualizamos la reserva
        attrMap.put(BookDao.STARTDATE, initialDate);
        attrMap.put(BookDao.ENDDATE, finalDate);
        return this.daoHelper.update(this.bookDao, attrMap, keyMap);
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
