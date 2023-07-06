package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IClientService;
import com.hotel.continental.model.core.dao.ClientDao;
import com.hotel.continental.model.core.dao.EmployeeDao;
import com.hotel.continental.model.core.dao.UserDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.*;

@Lazy
@Service("ClientService")
public class ClientService implements IClientService {
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private UserService userService;

    /**
     * Metodo que actualiza un cliente de la base de datos
     * Updatear por id? o updatear por filtro? ejemplo dni, o id o country code, o todos...
     *
     * @param attrMap Mapa con los campos de la clave
     * @param keyMap  Mapa con los campos de la clave
     * @return EntityResult con el id de los clientes o un mensaje de error
     */
    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult clientUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        EntityResult er = new EntityResultMapImpl();
        //Primero compruebo que el clientid existe dado que es necesario para la actualizacion
        if (keyMap.get(ClientDao.CLIENTID) == null) {
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        // Comprobar que el cliente exista
        EntityResult client = this.daoHelper.query(this.clientDao, keyMap, Arrays.asList(EmployeeDao.EMPLOYEEID, EmployeeDao.TUSER_NAME), ClientDao.CLIENT_INFO);
        if (client.calculateRecordNumber() == 0) {
            er.setCode(1);
            er.setMessage(ErrorMessages.EMPLOYEE_NOT_EXIST);
            return er;
        }
        //Updateamos el usuario
        Map<String, Object> filter = new HashMap<>();
        filter.put(UserDao.USER_, client.getRecordValues(0).get(EmployeeDao.TUSER_NAME));
        EntityResult check = userService.userUpdate(attrMap, filter);
        if (check.getCode() == EntityResult.OPERATION_WRONG) {
            return check;
        }
        //Updateamos el empleado
        er = this.daoHelper.update(this.clientDao, attrMap, keyMap);
        er.setMessage("Client updated succesfully");
        return er;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult clientDelete(Map<?, ?> keyMap) {
        EntityResult er = new EntityResultMapImpl();
        //Primero compruebo que el clientid existe dado que es necesario para la actualizacion
        if (keyMap.get(ClientDao.CLIENTID) == null) {
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Si el id del cliente no existe en la base de datos esta mal
        Map<String, Object> filter = new HashMap<>();
        filter.put(ClientDao.CLIENTID, keyMap.get(ClientDao.CLIENTID));
        EntityResult client = this.daoHelper.query(this.clientDao, filter, Arrays.asList(ClientDao.CLIENTID, ClientDao.TUSER_NAME));
        if (client.calculateRecordNumber() == 0) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.EMPLOYEE_NOT_EXIST);
            return er;
        }
        //Comprobamos que no esta dado de baja
        //Comprobamos que empleado(el usuario) no esté ya dado de baja
        Map<String, Object> filterUser = new HashMap<>();
        filterUser.put(UserDao.USER_, client.getRecordValues(0).get(ClientDao.TUSER_NAME));
        EntityResult erUser = this.daoHelper.query(this.clientDao, filterUser, Arrays.asList(UserDao.USERBLOCKED), ClientDao.CLIENT_INFO);
        if (erUser.getRecordValues(0).get(UserDao.USERBLOCKED) != null && Timestamp.valueOf(erUser.getRecordValues(0).get(UserDao.USERBLOCKED).toString()).before(new Timestamp(System.currentTimeMillis()))) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.CLIENT_ALREADY_INACTIVE);
            return er;
        }
        Map<Object, Object> keyMapUserDelete = new HashMap<>();//Mapa de atributos
        keyMapUserDelete.put(UserDao.USER_, client.getRecordValues(0).get(EmployeeDao.TUSER_NAME));//Añadimos el nombre de usuario
        //Devolvemos un entityResult que representa el éxito de la operación
        er = userService.userDelete(keyMapUserDelete);//Actualizamos el cliente
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        er.setMessage("Client terminated: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return er;
    }


    /**
     * Metodo que devuelve todos los clientes
     *
     * @param attrMap Mapa con los campos de la clave
     * @return EntityResult con los clientes o un mensaje de error
     */
    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult clientInsert(Map<String, Object> attrMap) {
        //Crear l mapa de atributos para usuario en un try catch?
        //Falta añadir rol cliente
        //El check comprueba que se inserto el tuser correctamente
        EntityResult check = userService.userInsert(attrMap);
        if (check.getCode() == EntityResult.OPERATION_WRONG) {
            return check;
        }
        Map<String, Object> userMap = new HashMap<>();
        userMap.put(ClientDao.TUSER_NAME, attrMap.get(UserDao.USER_));
        EntityResult er = this.daoHelper.insert(this.clientDao, userMap);
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        er.setMessage("Cliente insertado correctamente");
        return er;
    }


    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult clientQuery(Map<String, Object> keyMap, List<String> attrList) {
        if (attrList == null || attrList.isEmpty()) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_ATTR);
            return er;
        }

        //comprobamos que envio en el filtro un id,si lo envio y no existe el cliente devolvemos error
        EntityResult client = this.daoHelper.query(this.clientDao, keyMap, attrList, ClientDao.CLIENT_INFO);
        if (client == null || client.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.CLIENT_NOT_EXIST);
            return er;
        }
        //Quitamos la contraseña
        client.remove(UserDao.PASSWORD);
        client.put(UserDao.PASSWORD, Collections.nCopies(client.calculateRecordNumber(), "Protected"));
        return client;
    }

}
