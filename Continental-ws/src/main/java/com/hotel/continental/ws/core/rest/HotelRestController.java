package com.hotel.continental.ws.core.rest;

import com.hotel.continental.api.core.service.IHotelService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/hotel")
public class HotelRestController extends ORestController<IHotelService> {

    @Autowired
    private IHotelService hotelSrv;

    @Override
    public IHotelService getService() {
        return this.hotelSrv;
    }

}
