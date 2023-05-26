package com.hotel.Continental.ws.core.rest;

import com.hotel.Continental.api.core.service.IRoomService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/room")
public class RoomRestController extends ORestController<IRoomService> {

    @Autowired
    private IRoomService roomSrv;

    @Override
    public IRoomService getService() {
        return this.roomSrv;
    }

}