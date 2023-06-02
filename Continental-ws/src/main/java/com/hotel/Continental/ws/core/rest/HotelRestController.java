package com.hotel.Continental.ws.core.rest;

import com.hotel.Continental.api.core.service.IHotelService;
import com.hotel.Continental.api.core.service.IUserService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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
