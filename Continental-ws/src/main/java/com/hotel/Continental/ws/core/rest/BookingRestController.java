package com.hotel.Continental.ws.core.rest;

import com.hotel.Continental.api.core.service.IBookingService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
public class BookingRestController extends ORestController<IBookingService> {

    @Autowired
    private IBookingService bookSrv;

    @Override
    public IBookingService getService() {
        return this.bookSrv;
    }

}