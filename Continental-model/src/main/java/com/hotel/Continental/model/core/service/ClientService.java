package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IBookingService;
import com.hotel.Continental.api.core.service.IClientService;
import com.hotel.Continental.model.core.dao.BookingDao;
import com.hotel.Continental.model.core.dao.ClientDao;
import com.hotel.Continental.model.core.dao.RoomDao;
import com.ontimize.jee.common.db.AdvancedEntityResultMapImpl;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Lazy
@Service("ClientService")
public class ClientService implements IClientService {
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    /**
     * Metodo que devuelve todos los clientes
     * @param attrMap Mapa con los campos de la clave
     * @return EntityResult con los clientes o un mensaje de error
     */
    @Override
    public EntityResult clientInsert(Map<String, Object> attrMap) {
        //El check insert hace las comprobaciones oportunas
        EntityResult check = checkInsert(attrMap);
        if(check.getCode() == EntityResult.OPERATION_WRONG){
            return check;
        }
        EntityResult er= this.daoHelper.insert(this.clientDao, attrMap);
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        er.setMessage("Cliente insertado correctamente");
        return er;
    }



    /**
     * Metodo que hace las comprobaciones generales previas a un insert o update
     * @param attrMap Mapa con los campos de la clave
     * @return EntityResult con OPERATION_SUCCESSFUL o un mensaje de error
     */
    private EntityResult checkInsert(Map<String, Object> attrMap){
        //Hago esto para asegurarme de que el codigo de pais esta en mayusculas
        attrMap.put(ClientDao.COUNTRYCODE, ((String)attrMap.remove(ClientDao.COUNTRYCODE)).toUpperCase());
        //Si alguno de los campos necesarios esta vacio esta mal
        if(((String)attrMap.get(ClientDao.COUNTRYCODE)).isEmpty() || ((String)attrMap.get(ClientDao.NAME)).isEmpty() || ((String)attrMap.get(ClientDao.DOCUMENT)).isEmpty()){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("Alguno de los campos necesarios  estan vacio");
            return er;
        }
        //Si el country code no mide 2 Caracteres esta mal
        if(((String)attrMap.get(ClientDao.COUNTRYCODE)).length() != 2){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("El codigo de pais no tiene el formato correcto");
            return er;
        }
        //Si el country code no es un codigo de pais valido esta mal
        if(!checkCountryCode((String)((String) attrMap.get(ClientDao.COUNTRYCODE)))){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("El codigo de pais no es valido");
            return er;
        }
        //Si el documento no es valido esta mal
        if(!checkDocument((String)attrMap.get(ClientDao.DOCUMENT), (String)attrMap.get(ClientDao.COUNTRYCODE))){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("El documento no es valido");
            return er;
        }
        //Si el documento ya exite en la base de datos esta mal
        if(existsDocument((String)attrMap.get(ClientDao.DOCUMENT))){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage("El documento ya existe en la base de datos");
            return er;
        }
        EntityResult er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        return er;
    }

    /**
     * Metodo que comprueba si el documento es valido
     * @param document Documento
     * @param countryCode Codigo de pais
     * @return true si es valido, false si no lo es
     */
    private boolean checkDocument(String document, String countryCode){
        if(countryCode.equals("ES")){
            String dniRegex = "^\\d{8}[A-HJ-NP-TV-Z]$";
            if (!document.matches(dniRegex)) {
                return false;
            }
            String dniNumbers = document.substring(0, 8);
            String dniLetter = document.substring(8).toUpperCase();

            String validLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
            int dniMod = Integer.parseInt(dniNumbers) % 23;
            char calculatedLetter = validLetters.charAt(dniMod);
            return dniLetter.charAt(0) == calculatedLetter;
        }
        return true;
    }

    /**
     * Metodo que comprueba si el codigo de pais es valido
     * @param countryCode Codigo de pais
     * @return true si es valido, false si no lo es
     */
    private boolean checkCountryCode(String countryCode){
        String[] isoCountryCodes = Locale.getISOCountries();
        return Arrays.stream(isoCountryCodes).anyMatch(countryCode::equals);
    }

    /**
     * Metodo que comprueba si el documento ya existe en la base de datos
     * @param document Documento
     * @return true si existe, false si no existe
     */
    private boolean existsDocument(String document){
        Map<String, Object> keyMap = new HashMap<>();
        keyMap.put(ClientDao.DOCUMENT, document);
        List<Object> attrList = new ArrayList<>();
        attrList.add(ClientDao.DOCUMENT);
        EntityResult er=this.daoHelper.query(this.clientDao,keyMap, attrList);
        if(er.getCode() == EntityResult.OPERATION_SUCCESSFUL && er.calculateRecordNumber() > 0){
            return true;
        }
        return false;
    }
}
