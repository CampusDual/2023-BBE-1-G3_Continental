package com.hotel.Continental.ws.core.rest;

import com.hotel.Continental.api.core.service.IBookService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/book")
public class BookRestController extends ORestController<IBookService> {

    @Autowired
    private IBookService bookSrv;

    @Override
    public IBookService getService() {
        return this.bookSrv;
    }

}