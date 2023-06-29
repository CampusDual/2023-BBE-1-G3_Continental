package com.hotel.continental.ws.core.rest;

import com.hotel.continental.api.core.service.IAccessCardAssignmentService;
import com.hotel.continental.api.core.service.IBookingService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accesscardassignment")
public class AccessCardAssignmentRestController extends ORestController<IAccessCardAssignmentService> {
    @Autowired
    private IAccessCardAssignmentService accessCardAssignmentSrv;

    public IAccessCardAssignmentService getService() {
        return this.accessCardAssignmentSrv;
    }
}
