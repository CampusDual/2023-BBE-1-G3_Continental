package com.hotel.continental.ws.core.rest;

import com.hotel.continental.api.core.service.IBookingService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/book")
public class BookingRestController extends ORestController<IBookingService> {

    @Autowired
    private IBookingService bookingSrv;

    @Override
    public IBookingService getService() {
        return this.bookingSrv;
    }
    @PostMapping(value = "/checkin", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult checkin(@RequestBody Map<String, Object> req) {
        Map<String, Object> attr = ( Map<String, Object>) req.get("data");
        return this.getService().bookingCheckin(attr);
    }
    @PostMapping(value = "/checkout", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult checkout(@RequestBody Map<String, Object> req) {
        Map<String, Object> attr = ( Map<String, Object>) req.get("data");
        return this.getService().bookingCheckout(attr);
    }
    @PostMapping(value = "/price", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult bookingprice(@RequestBody Map<String, Object> req) {
        Map<String, Object> attr = ( Map<String, Object>) req.get("data");
        return this.getService().bookingPrice(attr);
    }

}