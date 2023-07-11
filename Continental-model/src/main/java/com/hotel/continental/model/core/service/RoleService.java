package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRoleService;
import com.hotel.continental.model.core.dao.RoleDao;
import com.hotel.continental.model.core.tools.Messages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.common.security.PermissionsProviderSecured;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import java.util.*;

@Lazy
@Service("RoleService")
public class RoleService implements IRoleService {
    @Autowired
    private RoleDao roleDao;

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roleQuery(Map<?, ?> keyMap, List<?> attrList) {
        EntityResult role = this.daoHelper.query(this.roleDao, keyMap, attrList);
        if(role == null || role.calculateRecordNumber() == 0){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.ROLE_DOESNT_EXIST);
            return er;
        }
        return role;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roleInsert(Map<String, Object> attrMap) {
        //Comnprobamos que nos mandan los atributos necesarios(rolename)
        if(!attrMap.containsKey(RoleDao.ROLENAME)){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }
        //Comprobamos que no esta vacio y que no es nulo
        if(attrMap.get(RoleDao.ROLENAME) == null || attrMap.get(RoleDao.ROLENAME).toString().isEmpty()){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }
        attrMap.put(RoleDao.ROLENAME, attrMap.remove(RoleDao.ROLENAME).toString().toLowerCase());
        attrMap.put(RoleDao.XMLCLIENTPERMISSION,"<?xml version=\"1.0\" encoding=\"UTF-8\"?><security></security>");
        //Primero comprobamos que el rol que nos pasan no existe
        EntityResult role = this.daoHelper.query(this.roleDao, attrMap, Arrays.asList(RoleDao.ROLENAME));
        if(role.calculateRecordNumber() > 0){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.ROLE_ALREADY_EXISTS);
            return er;
        }
        //Insertamos el rol
        return this.daoHelper.insert(this.roleDao, attrMap);
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roleDelete(Map<?, ?> keyMap) {
        if (!keyMap.containsKey(RoleDao.ID_ROLENAME)) {
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }
        if ((int) keyMap.get(RoleDao.ID_ROLENAME) == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.ADMIN_ROLE_NOT_EDITABLE);
            return er;
        }
        //si el rol no existe lanzar un error
        EntityResult role = roleQuery(keyMap, Arrays.asList(RoleDao.ID_ROLENAME));
        if (role.getCode() == EntityResult.OPERATION_WRONG) {
            return role;
        }
        EntityResult er = this.daoHelper.delete(this.roleDao, keyMap);
        er.setMessage("Role " + keyMap.get(RoleDao.ID_ROLENAME) + " deleted succesfully");
        return er;
    }

    @Override
    @Secured({ PermissionsProviderSecured.SECURED })
    public EntityResult roleUpdate(Map<String, Object> attrMap, Map<String, Object> keyMap) {
        //Comprobamos que nos manda la clave primaria o que no es nula o vacia
        if(!keyMap.containsKey(RoleDao.ID_ROLENAME) || keyMap.get(RoleDao.ID_ROLENAME) == null || keyMap.get(RoleDao.ID_ROLENAME).toString().isEmpty()){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos que nos mandan el rolename
        if(!attrMap.containsKey(RoleDao.ROLENAME)){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }
        //Ponemos el rolename en minusculas
        attrMap.put(RoleDao.ROLENAME, attrMap.remove(RoleDao.ROLENAME).toString().toLowerCase());
        //Comprobamos que ese rol existe
        EntityResult role = this.daoHelper.query(this.roleDao, keyMap, Arrays.asList(RoleDao.ID_ROLENAME));
        if(role.calculateRecordNumber() == 0){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.ROLE_DOESNT_EXIST);
            return er;
        }
        //Comprobamos que no hay otro rol con ese rolename
        EntityResult role2 = this.daoHelper.query(this.roleDao, attrMap, Arrays.asList(RoleDao.ROLENAME));
        if(role2.calculateRecordNumber() > 0){
            EntityResult er;
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.ROLE_ALREADY_EXISTS);
            return er;
        }
        //Actualizamos el rol
        EntityResult er = this.daoHelper.update(this.roleDao, attrMap, keyMap);
        er.setMessage("Role updated succesfully");
        return er;
    }
}
