package com.hotel.Continental.ws.core.rest;

import com.hotel.Continental.api.core.service.IClientService;
import com.hotel.Continental.api.core.service.IRoomService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/client")
public class ClientRestController extends ORestController<IClientService> {

    @Autowired
    private IClientService clientSrv;

    @Override
    public IClientService getService() {
        return this.clientSrv;
    }

}