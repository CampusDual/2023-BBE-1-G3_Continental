package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IEmployeeService;
import com.hotel.continental.model.core.dao.EmployeeDao;
import com.hotel.continental.model.core.dao.HotelDao;
import com.hotel.continental.model.core.tools.Messages;
import com.hotel.continental.model.core.tools.Validation;
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
@Service("EmployeeService")
public class EmployeeService implements IEmployeeService {

    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    HotelDao hotelDao;
    @Autowired
    DefaultOntimizeDaoHelper daoHelper;

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeInsert(Map<?, ?> attrMap) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(1);
        if (!attrMap.containsKey(EmployeeDao.HOTEL_ID) || !attrMap.containsKey(EmployeeDao.DOCUMENT) || !attrMap.containsKey(EmployeeDao.NAME)) {
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }

        // Comprueba que el hotel existe
        Map<String, Object> filter = new HashMap<>();
        filter.put(HotelDao.HOTEL_ID, attrMap.get(EmployeeDao.HOTEL_ID));
        EntityResult hotel = this.daoHelper.query(this.hotelDao, filter, Arrays.asList(HotelDao.HOTEL_ID));

        if (hotel.calculateRecordNumber() == 0) {
            er.setMessage(Messages.HOTEL_NOT_EXIST);
            return er;
        }
        //Comprobamos que el documento es válido
        if (attrMap.get(EmployeeDao.DOCUMENT) != null && !Validation.checkDocument((String) attrMap.get(EmployeeDao.DOCUMENT), "ES")) {
            //Si el documento no es valido esta mal
                er.setMessage(Messages.DOCUMENT_NOT_VALID);
                return er;
        }
        //Check que no existe el nif
        filter = new HashMap<>();
        filter.put(EmployeeDao.DOCUMENT, attrMap.get(EmployeeDao.DOCUMENT));
        EntityResult nif = this.daoHelper.query(this.employeeDao, filter, Arrays.asList(EmployeeDao.DOCUMENT));

        if (nif.calculateRecordNumber() > 0) {
            er.setMessage(Messages.EMPLOYEE_ALREADY_EXIST);
            return er;
        }

        return this.daoHelper.insert(this.employeeDao, attrMap);
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(EntityResult.OPERATION_WRONG);

        // Comprobar que los mapas no están vacios
        if (attrMap.isEmpty() || keyMap.isEmpty()) {
            er.setMessage(Messages.NECESSARY_DATA);
            return er;
        }
        //Comprobamos que nos envia un EmployeeId
        if (!keyMap.containsKey(EmployeeDao.EMPLOYEE_ID)) {
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos que el documento es válido
        if (attrMap.get(EmployeeDao.DOCUMENT) != null && !Validation.checkDocument((String) attrMap.get(EmployeeDao.DOCUMENT), "ES")) {
            //Si el documento no es valido esta mal
                er.setMessage(Messages.DOCUMENT_NOT_VALID);
                return er;
        }

        // Comprobar que el empleado exista
        EntityResult employee = this.daoHelper.query(this.employeeDao, keyMap, Arrays.asList(EmployeeDao.EMPLOYEE_ID));
        if (employee.calculateRecordNumber() == 0) {
            er.setMessage(Messages.EMPLOYEE_NOT_EXIST);
            return er;
        }
        er = this.daoHelper.update(this.employeeDao, attrMap, keyMap);
        er.setMessage("Employee updated succesfully");
        return er;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeQuery(Map<?, ?> keyMap, List<?> attrList) {
        if (keyMap.containsKey(EmployeeDao.EMPLOYEE_ID) || keyMap.containsKey(EmployeeDao.HOTEL_ID)) {
            EntityResult employees = this.daoHelper.query(this.employeeDao, keyMap, attrList);
            if (employees.calculateRecordNumber() == 0) {
                EntityResult er = new EntityResultMapImpl();
                er.setCode(EntityResult.OPERATION_WRONG);
                er.setMessage(Messages.EMPLOYEE_NOT_EXIST);
                return er;
            }
            return employees;
        }
        return this.daoHelper.query(this.employeeDao, keyMap, attrList);
    }

    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeDelete(Map<?, ?> keyMap) {
        EntityResult er;
        //Comprobamos que nos envia un id
        if (!keyMap.containsKey(EmployeeDao.EMPLOYEE_ID)) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos que el empleado existe
        //Si no existe, devolvemos un entityResult que representa un error
        Map<String, Object> filter = new HashMap<>();
        filter.put(EmployeeDao.EMPLOYEE_ID, keyMap.get(EmployeeDao.EMPLOYEE_ID));
        EntityResult employee = this.daoHelper.query(this.employeeDao, filter, Arrays.asList(EmployeeDao.EMPLOYEE_ID, EmployeeDao.EMPLOYEE_DOWN_DATE));
        if (employee.calculateRecordNumber() == 0) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.EMPLOYEE_NOT_EXIST);
            return er;
        }

        //Comprobamos que el empleado esta en activo
        if (employee.getRecordValues(0).get(EmployeeDao.EMPLOYEE_DOWN_DATE) != null) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(Messages.EMPLOYEE_ALREADY_INACTIVE);
            return er;
        }

        Map<Object, Object> attrMap = new HashMap<>();//Mapa de atributos
        attrMap.put(EmployeeDao.EMPLOYEE_DOWN_DATE, new Timestamp(System.currentTimeMillis()));//Añadimos la fecha de baja
        //Devolvemos un entityResult que representa el éxito de la operación
        er = this.daoHelper.update(this.employeeDao, attrMap, keyMap);//Actualizamos el empleado
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        er.setMessage("Employee terminated: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return er;
    }
}

