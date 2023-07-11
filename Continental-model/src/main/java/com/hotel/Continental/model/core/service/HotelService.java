package com.hotel.continental.model.core.service;

import com.hotel.continental.model.core.tools.Messages;
import com.hotel.continental.api.core.service.IHotelService;
import com.hotel.continental.model.core.dao.HotelDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.apache.logging.log4j.util.Strings;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
     *
     * @param keyMap   Mapa de claves que identifican el hotel
     * @param attrList Lista de atributos que se quieren obtener
     * @return EntityResult con los datos del hotel o un mensaje de error
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult hotelQuery(Map<?, ?> keyMap, List<?> attrList) {
        EntityResult er;
        er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_WRONG);

        //Comprobar que los parámetros no esten vacios
        if(attrList.isEmpty()) {
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }
        //Comprobar hotel que el hotel existe
        EntityResult hotel = this.daoHelper.query(this.hotelDao, keyMap, attrList);
        if (hotel == null || hotel.calculateRecordNumber() == 0) {
            er.setMessage(Messages.HOTEL_NOT_EXIST);
            return er;
        }
        return hotel;
    }

    /**
     * Método inserta un hotel en la base de datos y devuelve un EntityResult con los datos del hotel
     *
     * @param attrMap Mapa de atributos que se quieren obtener
     * @return EntityResult con los datos del hotel o un mensaje de error
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult hotelInsert(Map<?, ?> attrMap) {
        if (attrMap.get(HotelDao.NAME) == null || attrMap.get(HotelDao.ADDRESS) == null ||
                !attrMap.containsKey(HotelDao.NAME) || !attrMap.containsKey(HotelDao.ADDRESS)
        || Strings.isEmpty((String) attrMap.get(HotelDao.NAME)) || Strings.isEmpty((String) attrMap.get(HotelDao.ADDRESS))) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }
        return this.daoHelper.insert(hotelDao, attrMap);
    }

    /**
     * Método actualiza los datos de un hotel y devuelve un EntityResult con los datos del hotel
     *
     * @param keyMap Mapa de claves que identifican el hotel
     * @return EntityResult que representa el éxito o fracaso de la operación
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult hotelUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        //Comprobamos mull key
        if(keyMap.get(HotelDao.ID) == null){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos que el hotel existe
        //Si no existe, devolvemos un entityResult que representa un error
        EntityResult hotel = hotelQuery(keyMap, Arrays.asList(HotelDao.ID, HotelDao.HOTELDOWNDATE));
        EntityResult er;
        if (hotel.getCode() == EntityResult.OPERATION_WRONG) {
            return hotel;
        }
        //Sino hacemos la actualización
        er = this.daoHelper.update(this.hotelDao, attrMap, keyMap);
        return er;
    }

    /**
     * Método que da de baja un hotel y devuelve un EntityResult con los datos del hotel
     *
     * @param keyMap Mapa de claves que identifican el hotel
     * @return EntityResult que representa el éxito o fracaso de la operación
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult hotelDelete(Map<?, ?> keyMap) {
        EntityResult er;
        //Comprobamos que nos envia un id
        if (!keyMap.containsKey(HotelDao.ID)) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos que el hotel existe
        //Si no existe, devolvemos un entityResult que representa un error
        EntityResult hotel = hotelQuery(keyMap, Arrays.asList(HotelDao.ID, HotelDao.HOTELDOWNDATE));
        if (hotel.getCode() == EntityResult.OPERATION_WRONG) {
            return hotel;
        }
        //Comprobamos que el hotel esta en activo
        if (hotel.getRecordValues(0).get(HotelDao.HOTELDOWNDATE) != null) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.HOTEL_ALREADY_INACTIVE);
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

