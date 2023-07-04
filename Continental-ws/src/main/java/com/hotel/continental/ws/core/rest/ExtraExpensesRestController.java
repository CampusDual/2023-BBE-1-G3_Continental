package com.hotel.continental.ws.core.rest;

import com.hotel.continental.api.core.service.IExtraExpensesService;
import com.ontimize.jee.server.rest.ORestController;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/extraexpenses")
public class ExtraExpensesRestController extends ORestController<IExtraExpensesService> {
    @Autowired
    private IExtraExpensesService extraExpensesSrv;

    @Override
    public IExtraExpensesService getService() {
        return this.extraExpensesSrv;
    }
}
