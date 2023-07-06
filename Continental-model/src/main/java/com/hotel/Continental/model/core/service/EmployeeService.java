package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IEmployeeService;
import com.hotel.continental.model.core.dao.EmployeeDao;
import com.hotel.continental.model.core.dao.HotelDao;
import com.hotel.continental.model.core.dao.RoleDao;
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
@Service("EmployeeService")
public class EmployeeService implements IEmployeeService {

    @Autowired
    EmployeeDao employeeDao;
    @Autowired
    HotelDao hotelDao;
    @Autowired
    DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    UserService userService;

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeInsert(Map<?, ?> attrMap) {
        if (!attrMap.containsKey(EmployeeDao.IDHOTEL) || !attrMap.containsKey(RoleDao.ID_ROLENAME)) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        // Comprueba que el hotel existe
        Map<String, Object> filter = new HashMap<>();
        filter.put(HotelDao.ID, attrMap.get(EmployeeDao.IDHOTEL));
        EntityResult hotel = this.daoHelper.query(this.hotelDao, filter, Arrays.asList(HotelDao.ID));
        if (hotel.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.HOTEL_NOT_EXIST);
            return er;
        }
        //Crear un mapa para el usuario con un try catch?
        //Insertamos el usuario
        EntityResult check = userService.userInsert(attrMap);
        if (check.getCode() == EntityResult.OPERATION_WRONG) {
            return check;
        }
        Map<String, Object> employeeMap = new HashMap<>();
        employeeMap.put(EmployeeDao.TUSER_NAME, attrMap.get(UserDao.USER_));
        employeeMap.put(EmployeeDao.IDHOTEL, attrMap.get(EmployeeDao.IDHOTEL));
        EntityResult er = this.daoHelper.insert(this.employeeDao, employeeMap);
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        er.setMessage("Empleado insertado correctamente");
        return er;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeUpdate(Map<?, ?> attrMap, Map<?, ?> keyMap) {
        EntityResult er = new EntityResultMapImpl();

        // Comprobar que los mapas no están vacios
        if (attrMap.isEmpty() || keyMap.isEmpty()) {
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }

        //Comprobamos que nos envia un EmployeeId
        if (!keyMap.containsKey(EmployeeDao.EMPLOYEEID)) {
            er.setCode(1);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }

        // Comprobar que el empleado exista
        EntityResult employee = this.daoHelper.query(this.employeeDao, keyMap, Arrays.asList(EmployeeDao.EMPLOYEEID));
        if (employee.calculateRecordNumber() == 0) {
            er.setCode(1);
            er.setMessage(ErrorMessages.EMPLOYEE_NOT_EXIST);
            return er;
        }
        er = this.daoHelper.update(this.employeeDao, attrMap, keyMap);
        er.setMessage("Employee updated succesfully");
        return er;
    }

    @Override
    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeQuery(Map<?, ?> keyMap, List<?> attrList) {
        EntityResult employees = this.daoHelper.query(this.employeeDao, keyMap, attrList, EmployeeDao.EMPLOYEE_INFO);
        if (employees.calculateRecordNumber() == 0) {
            EntityResult er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.EMPLOYEE_NOT_EXIST);
            return er;
        }
        //Quitamos la contraseña
        employees.remove(UserDao.PASSWORD);
        employees.put(UserDao.PASSWORD, Collections.nCopies(employees.calculateRecordNumber(), "Protected"));
        return employees;
    }

    @Secured({PermissionsProviderSecured.SECURED})
    public EntityResult employeeDelete(Map<?, ?> keyMap) {
        EntityResult er;
        //Comprobamos que nos envia un id
        if (!keyMap.containsKey(EmployeeDao.EMPLOYEEID)) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_KEY);
            return er;
        }
        //Comprobamos que el empleado existe
        //Si no existe, devolvemos un entityResult que representa un error
        Map<String, Object> filter = new HashMap<>();
        filter.put(EmployeeDao.EMPLOYEEID, keyMap.get(EmployeeDao.EMPLOYEEID));
        EntityResult employee = this.daoHelper.query(this.employeeDao, filter, Arrays.asList(EmployeeDao.EMPLOYEEID,EmployeeDao.TUSER_NAME));
        if (employee.calculateRecordNumber() == 0) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.EMPLOYEE_NOT_EXIST);
            return er;
        }

        //Comprobamos que empleado(el usuario) no esté ya dado de baja
        Map<String, Object> filterUser = new HashMap<>();
        filterUser.put(UserDao.USER_, employee.getRecordValues(0).get(EmployeeDao.TUSER_NAME));
        EntityResult erUser=this.daoHelper.query(this.employeeDao,filterUser,Arrays.asList(UserDao.USERBLOCKED),EmployeeDao.EMPLOYEE_INFO);
        if (erUser.getRecordValues(0).get(UserDao.USERBLOCKED) != null && Timestamp.valueOf(erUser.getRecordValues(0).get(UserDao.USERBLOCKED).toString()).before(new Timestamp(System.currentTimeMillis()))) {
            er = new EntityResultMapImpl();
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.EMPLOYEE_ALREADY_INACTIVE);
            return er;
        }
        Map<Object, Object> keyMapUserDelete = new HashMap<>();//Mapa de atributos
        keyMapUserDelete.put(UserDao.USER_, employee.getRecordValues(0).get(EmployeeDao.TUSER_NAME));//Añadimos el nombre de usuario
        //Devolvemos un entityResult que representa el éxito de la operación
        er = userService.userDelete(keyMapUserDelete);//Actualizamos el empleado
        er.setCode(EntityResult.OPERATION_SUCCESSFUL);
        er.setMessage("Employee terminated: " + new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        return er;
    }
}

