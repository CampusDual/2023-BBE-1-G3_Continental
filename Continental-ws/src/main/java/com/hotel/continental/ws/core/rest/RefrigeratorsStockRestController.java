package com.hotel.continental.ws.core.rest;

import com.hotel.continental.api.core.service.IRefrigeratorStockService;
import com.hotel.continental.api.core.service.IRefrigeratorsService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/refrigeratorstock")
public class RefrigeratorsStockRestController extends ORestController<IRefrigeratorStockService> {
    @Autowired
    private IRefrigeratorStockService refrigeratorStockService;
    @Override
    public IRefrigeratorStockService getService() {
        return this.refrigeratorStockService;
    }

    @PutMapping(value = "/refrigeratorDefault", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult refrigeratorDefaultUpdate(@RequestBody Map<String, Object> req) {
        Map<String, Object> attr = ( Map<String, Object>) req.get("data");
        return this.getService().refrigeratorDefaultUpdate(attr);
    }
}
