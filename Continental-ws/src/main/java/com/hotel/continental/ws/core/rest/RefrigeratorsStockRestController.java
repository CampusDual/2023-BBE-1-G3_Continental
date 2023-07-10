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
}
