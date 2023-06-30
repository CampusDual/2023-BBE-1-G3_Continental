package com.hotel.continental.ws.core.rest;

import com.hotel.continental.api.core.service.ICriteriaService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.beans.factory.annotation.Autowired;
@RestController
@RequestMapping("/criteria")
public class CriteriaController extends ORestController<ICriteriaService> {

        @Autowired
        private ICriteriaService service;

        @Override
        public ICriteriaService getService() {
            return this.service;
        }
}
