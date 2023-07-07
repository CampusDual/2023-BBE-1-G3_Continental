package com.hotel.continental.model.core.service;


import com.hotel.continental.api.core.service.IUserService;
import com.hotel.continental.model.core.dao.ClientDao;
import com.hotel.continental.model.core.dao.RoleDao;
import com.hotel.continental.model.core.dao.UserDao;
import com.hotel.continental.model.core.dao.UserRoleDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import com.ontimize.jee.server.dao.IOntimizeDaoSupport;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;


@Lazy
@Service("UserService")
public class UserService implements IUserService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private UserRoleDao userRoleDao;

    @Autowired
    private RoleDao roleDao;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;

    public void loginQuery(Map<?, ?> key, List<?> attr) {
        throw new UnsupportedOperationException();
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userQuery(Map<?, ?> keyMap, List<?> attrList) {
        return this.daoHelper.query(userDao, keyMap, attrList);
    }

    @Override
    public EntityResult userInsert(Map<?, ?> attrMap) {
        //Comprobamos que los datos necesarios estén en el map
        if (!attrMap.containsKey(UserDao.USER_) || !attrMap.containsKey(UserDao.PASSWORD) || !attrMap.containsKey(UserDao.NAME) ||
                !attrMap.containsKey(UserDao.SURNAME)|| !attrMap.containsKey(UserDao.NIF)||!attrMap.containsKey(UserDao.COUNTRYCODE)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        EntityResult check=checkUpdate((Map<String, Object>) attrMap);
        if(check.getCode()==EntityResult.OPERATION_WRONG){
            return check;
        }
        //Insertamos el usuario
        EntityResult user = this.daoHelper.insert(this.userDao, attrMap);
        //Si tiene rol lo insertamos
        if(attrMap.containsKey(RoleDao.ID_ROLENAME)) {
            Map<String, Object> attrRole = new HashMap<>();
            attrRole.put(UserRoleDao.ID_ROLENAME, attrMap.get(RoleDao.ID_ROLENAME));
            attrRole.put(UserRoleDao.USER, attrMap.get(UserDao.USER_));
            //Insertamos el rol del usuario
            Map<String, Object> userRole = new HashMap<>();
            userRole.put(UserRoleDao.USER, attrMap.get(UserDao.USER_));
            userRole.put(UserRoleDao.ID_ROLENAME, attrMap.get(RoleDao.ID_ROLENAME));
            this.daoHelper.insert(this.userRoleDao, userRole);
        }
        return user;
    }
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        //para editarlo comprobamos
        if(attrMap == null || attrMap.isEmpty()){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Comprobamos que nos envia la clave
        if(!keyMap.containsKey(UserDao.USER_)){
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }

        EntityResult queryUser = this.daoHelper.query(this.userDao, keyMap, Arrays.asList(UserDao.USER_));
        if(queryUser.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.USER_DOESNT_EXIST);
            return er;
        }
        //comprobamos los datos
        EntityResult check=checkUpdate((Map<String, Object>) attrMap);
        if(check.getCode()==EntityResult.OPERATION_WRONG){
            return check;
        }
        //Actualizamos el usuario
        return this.daoHelper.update(this.userDao, attrMap, keyMap);
    }
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult userDelete(Map<?, ?> keyMap) {
        if (!keyMap.containsKey(UserDao.USER_)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }

        EntityResult query = this.daoHelper.query(this.userDao, keyMap, Arrays.asList(UserDao.USER_));
        if (query.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.USER_DOESNT_EXIST);
            return er;
        }

        Map<Object, Object> attrMap = new HashMap<>();
        attrMap.put(UserDao.USERBLOCKED, new Timestamp(Calendar.getInstance().getTimeInMillis()));
        return this.daoHelper.update(this.userDao, attrMap, keyMap);
    }

    /**
     * Metodo que hace las comprobacioes previas a un insert/update
     *
     * @param attrMap Mapa con los campos de la clave
     * @return EntityResult con OPERATION_SUCCESSFUL o un mensaje de error
     */
    private EntityResult checkUpdate(Map<String, Object> attrMap) {
        //Hago esto para asegurarme de que el codigo de pais esta en mayusculas y que no sea nulo
        if (attrMap.get(UserDao.COUNTRYCODE) != null) {
            attrMap.put(UserDao.COUNTRYCODE, ((String) attrMap.remove(UserDao.COUNTRYCODE)).toUpperCase());
            //Si el country code no mide 2 Caracteres esta mal
            if (((String) attrMap.get(UserDao.COUNTRYCODE)).length() != 2) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.COUNTRY_CODE_FORMAT_ERROR);
                return er;
            }
            //Si el country code no es un codigo de pais valido esta mal
            if (!checkCountryCode(((String) attrMap.get(UserDao.COUNTRYCODE)))) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.COUNTRY_CODE_NOT_VALID);
                return er;
            }
        }
        if (attrMap.get(UserDao.NIF) != null) {
            //Si el documento no es valido esta mal
            if (!checkDocument((String) attrMap.get(UserDao.NIF), (String) attrMap.get(UserDao.COUNTRYCODE))) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.DOCUMENT_NOT_VALID);
                return er;
            }
            //Si el documento ya exite en la base de datos esta mal
            if (existsKeymap(this.userDao,Collections.singletonMap(UserDao.NIF, attrMap.get(UserDao.NIF)),List.of(UserDao.NIF))) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.DOCUMENT_ALREADY_EXIST);
                return er;
            }
            if (existsKeymap(this.userDao,Collections.singletonMap(UserDao.USER_, attrMap.get(UserDao.USER_)),List.of(UserDao.USER_))) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.USER_ALREADY_EXIST);
                return er;
            }
        }
        if(attrMap.get(UserDao.PASSWORD)!=null){
            if (!attrMap.get(UserDao.PASSWORD).toString().matches("^(?=.*[A-Z])(?=.*\\d).{8,}$")){
                EntityResult er = new EntityResultMapImpl();
                er.setCode(1);
                er.setMessage(ErrorMessages.INCORRECT_PASSWORD);
                return er;
            }
        }
        if(attrMap.get(RoleDao.ID_ROLENAME)!=null){
            if(!existsKeymap(this.roleDao,Collections.singletonMap(RoleDao.ID_ROLENAME, attrMap.get(RoleDao.ID_ROLENAME)),List.of(RoleDao.ID_ROLENAME))){
                EntityResult er = new EntityResultMapImpl();
                er.setCode(1);
                er.setMessage(ErrorMessages.ROLE_DOESNT_EXIST);
                return er;
            }
        }
        EntityResult er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        return er;
    }

    /**
     * Metodo que comprueba si el documento es valido
     *
     * @param document    Documento
     * @param countryCode Codigo de pais
     * @return true si es valido, false si no lo es
     */
    private boolean checkDocument(String document, String countryCode) {
        if (countryCode.equals("ES")) {
            String dniRegex = "^\\d{8}[A-HJ-NP-TV-Z]$";
            String nieRegex = "^[XYZ]\\d{7}[A-Z]$";
            String cifRegex = "^([ABCDEFGHJKLMNPQRSUVW])(\\d{7})([0-9A-J])$";
            if (document.matches(dniRegex)) {
                String dniNumbers = document.substring(0, 8);
                String dniLetter = document.substring(8).toUpperCase();

                String validLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
                int dniMod = Integer.parseInt(dniNumbers) % 23;
                char calculatedLetter = validLetters.charAt(dniMod);
                return dniLetter.charAt(0) == calculatedLetter;
            }
            String firstLetter = document.substring(0,1);
            if ((firstLetter.equals("Z") || firstLetter.equals("X") || firstLetter.equals("Y")) && document.matches(nieRegex)) {
                String nieNumbers = document.substring(1, 8);
                String validLetters = "TRWAGMYFPDXBNJZSQVHLCKE";
                String lastLetter = document.substring(8);
                int nieMod = Integer.parseInt(nieNumbers) % 23;
                char calculatedLetter = validLetters.charAt(nieMod);
                return lastLetter.charAt(0) == calculatedLetter;

            }
            return document.matches(cifRegex);
        }
        return true;
    }

    /**
     * Metodo que comprueba si el codigo de pais es valido
     *
     * @param countryCode Codigo de pais
     * @return true si es valido, false si no lo es
     */
    private boolean checkCountryCode(String countryCode) {
        String[] isoCountryCodes = Locale.getISOCountries();
        return Arrays.stream(isoCountryCodes).anyMatch(countryCode::equals);
    }

    /**
     * Metodo que comprueba si el keyMap ya existe en la base de datos
     *
     * @param keyMap Mapa con los campos de la clave
     * @return true si existe, false si no existe
     */

    private boolean existsKeymap(IOntimizeDaoSupport dao,Map<String, Object> keyMap, List<Object> attrList) {
        EntityResult er = this.daoHelper.query(dao, keyMap, attrList);
        System.out.println(er.getMessage());
        return er.getCode() == EntityResult.OPERATION_SUCCESSFUL && er.calculateRecordNumber() > 0;
    }

}
