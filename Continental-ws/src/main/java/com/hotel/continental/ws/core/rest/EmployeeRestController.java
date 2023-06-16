package com.hotel.continental.ws.core.rest;

import com.hotel.continental.api.core.service.IEmployeeService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/employee")
public class EmployeeRestController extends ORestController<IEmployeeService> {

    @Autowired
    private IEmployeeService employeeSrv;

    @Override
    public IEmployeeService getService() {
        return this.employeeSrv;
    }
}
