package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IHotelService;
import com.hotel.Continental.api.core.service.IUserService;
import com.hotel.Continental.model.core.dao.BookDao;
import com.hotel.Continental.model.core.dao.HotelDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;

@Lazy
@Service("HotelService")
public class HotelService implements IHotelService {

    @Autowired
    private HotelDao hotelDao;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    /**
     * Metodo que devuelve un EntityResult con los datos del hotel
     * @param keyMap Mapa de claves que identifican el hotel
     * @param attrList Lista de atributos que se quieren obtener
     * @return EntityResult con los datos del hotel o un mensaje de error
     */
    public EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList) {
        EntityResult hotel = this.daoHelper.query(this.hotelDao, keyMap, attrList);
        if(hotel.calculateRecordNumber() == 0){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("El hotel no existe");
            return er;
        }
        return hotel;
    }

    /**
     * Método inserta un hotel en la base de datos y devuelve un EntityResult con los datos del hotel
     * @param attrMap Mapa de atributos que se quieren obtener
     * @return EntityResult con los datos del hotel o un mensaje de error
     */
    public EntityResult hotelInsert(Map<?, ?> attrMap) {
        return this.daoHelper.insert(hotelDao, attrMap);
    }

    /**
     * Método actualiza los datos de un hotel y devuelve un EntityResult con los datos del hotel
     * @param keyMap Mapa de claves que identifican el hotel
     * @return EntityResult que representa el éxito o fracaso de la operación
     */
    @Override
    public EntityResult hotelUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        //Comprobamos que el hotel existe
        //Si no existe, devolvemos un entityResult que representa un error
        EntityResult hotel = hotelQuery(keyMap, Arrays.asList(HotelDao.ID, HotelDao.HOTELDOWNDATE));
        EntityResult er;
        if(hotel.getCode() == EntityResult.OPERATION_WRONG){
            return hotel;
        }
        //Sino hacemos la actualización
        er=this.daoHelper.update(this.hotelDao, attrMap, keyMap);
        return er;
    }

    /**
     * Método que da de baja un hotel y devuelve un EntityResult con los datos del hotel
     * @param keyMap Mapa de claves que identifican el hotel
     * @return EntityResult que representa el éxito o fracaso de la operación
     */
    public EntityResult hotelDelete(Map<?, ?> keyMap){
        //Comprobamos que el hotel existe
        //Si no existe, devolvemos un entityResult que representa un error
        EntityResult hotel = hotelQuery(keyMap, Arrays.asList(HotelDao.ID, HotelDao.HOTELDOWNDATE));
        EntityResult er;
        if(hotel.getCode() == EntityResult.OPERATION_WRONG){
            return hotel;
        }
        //Comprobamos que el hotel esta en activo
        if(hotel.getRecordValues(0).get(HotelDao.HOTELDOWNDATE) != null){
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("El hotel ya esta dado de baja");
            return er;
        }
        Map<Object, Object> attrMap = new HashMap<>();//Mapa de atributos
        attrMap.put(HotelDao.HOTELDOWNDATE, new Timestamp(System.currentTimeMillis()));//Añadimos la fecha de baja
        //Devolvemos un entityResult que representa el éxito de la operación
        er = this.daoHelper.update(this.hotelDao, attrMap, keyMap);//Actualizamos el hotel
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        er.setMessage("Hotel dado de baja correctamente con fecha " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return er;
    }

}

