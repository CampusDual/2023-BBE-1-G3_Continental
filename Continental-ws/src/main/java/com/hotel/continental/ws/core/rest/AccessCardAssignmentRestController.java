package com.hotel.continental.ws.core.rest;

import com.hotel.continental.api.core.service.IAccessCardAssignmentService;
import com.hotel.continental.api.core.service.IBookingService;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/accesscardassignment")
public class AccessCardAssignmentRestController extends ORestController<IAccessCardAssignmentService> {
    @Autowired
    private IAccessCardAssignmentService accessCardAssignmentSrv;

    public IAccessCardAssignmentService getService() {
        return this.accessCardAssignmentSrv;
    }

    @PostMapping(value = "/lostCard", produces = MediaType.APPLICATION_JSON_VALUE)
    public EntityResult lostCard(@RequestBody Map<String, Object> req) {
        Map<String, Object> attr = (Map<String, Object>) req.get("data");
        return this.getService().lostCard(attr);
    }
}
