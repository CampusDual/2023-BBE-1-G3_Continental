package com.hotel.continental.ws.core.rest;

import com.hotel.continental.api.core.service.IRefrigeratorsService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
    @RequestMapping("/refrigerators")
public class RefrigeratorsRestController extends ORestController<IRefrigeratorsService> {
    @Autowired
    private IRefrigeratorsService refrigeratorsService;

    @Override
    public IRefrigeratorsService getService() {
        return this.refrigeratorsService;
    }
}
