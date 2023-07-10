package com.hotel.continental.ws.core.rest;

import com.hotel.continental.api.core.service.IParkingHistoryService;
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
@RequestMapping("/parkingHistory")
public class ParkingHistoryRestController extends ORestController<IParkingHistoryService> {
    @Autowired
    private IParkingHistoryService parkingHistorySrv;

    @Override
    public IParkingHistoryService getService() {
        return this.parkingHistorySrv;
    }

}
