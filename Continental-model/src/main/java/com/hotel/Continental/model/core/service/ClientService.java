package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IClientService;
import com.hotel.continental.model.core.dao.ClientDao;
import com.hotel.continental.model.core.dao.UserDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.userdetails.User;
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
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult clientUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        /*
        //Primero compruebo que el clientid existe dado que es necesario para la actualizacion
        if (keyMap.get(ClientDao.CLIENTID) == null) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Si el id del cliente no existe en la base de datos esta mal
        if (!existsKeymap(Collections.singletonMap(ClientDao.CLIENTID, keyMap.get(ClientDao.CLIENTID)))) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.CLIENT_NOT_EXIST);
            return er;
        }
        //El check update hace las comprobaciones de los datos a insertar
        EntityResult checkDatos = checkUpdate(attrMap);
        if (checkDatos.getCode() == EntityResult.OPERATION_WRONG) {
            return checkDatos;
        }
        EntityResult er = this.daoHelper.update(this.clientDao, attrMap, keyMap);
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        return er;
         */
        return null;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult clientDelete(Map<?, ?> keyMap) {
       /* EntityResult er = new EntityResultMapImpl();
        //Comprobar que se envia el id del cliente
        if (keyMap.get(ClientDao.CLIENTID) == null) {
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
        }
        if (!existsKeymap(Collections.singletonMap(ClientDao.CLIENTID, keyMap.get(ClientDao.CLIENTID)))) {
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.CLIENT_NOT_EXIST);
        } else if (isCanceled(keyMap)) {
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.CLIENT_ALREADY_DELETED);
        } else {
            Map<String, Object> attrMap = new HashMap<>();
            attrMap.put(ClientDao.CLIENTDOWNDATE, new Timestamp(System.currentTimeMillis()));
            er = this.daoHelper.update(clientDao, attrMap, keyMap);
            er.setCode(EntityResult.OPERATION_SUCCESSFUL);
            er.setMessage("Este cliente se ha dado de baja con fecha " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        }
        return er;
                    */
        return null;
    }


    /**
     * Metodo que devuelve todos los clientes
     *
     * @param attrMap Mapa con los campos de la clave
     * @return EntityResult con los clientes o un mensaje de error
     */
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
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
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult clientQuery(Map<String, Object> keyMap, List<?> attrList) {
        //comprobamos que envio en el filtro un id,si lo envio y no existe el cliente devolvemos error
            EntityResult client = this.daoHelper.query(this.clientDao, keyMap, attrList);
            if(client == null || client.calculateRecordNumber() == 0) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(ErrorMessages.CLIENT_NOT_EXIST);
                return er;
            }
            return client;
    }
}
