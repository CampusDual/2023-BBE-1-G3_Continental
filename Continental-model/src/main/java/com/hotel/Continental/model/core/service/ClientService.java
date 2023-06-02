package com.hotel.Continental.model.core.service;

import com.hotel.Continental.api.core.service.IBookingService;
import com.hotel.Continental.api.core.service.IClientService;
import com.hotel.Continental.model.core.dao.BookingDao;
import com.hotel.Continental.model.core.dao.ClientDao;
import com.hotel.Continental.model.core.dao.RoomDao;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Lazy
@Service("ClientService")
public class ClientService implements IClientService {
    @Autowired
    private ClientDao clientDao;
    @Autowired
    private ClientService clientService;
    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;


    @Override
    public EntityResult clientQuery(Map<String, Object> keyMap, List<?> attrList) {
        return null;
    }

    @Override
    public EntityResult clientInsert(Map<String, Object> attrMap) {
        return null;
    }

    @Override
    public EntityResult clientDelete(Map<String, Object> keyMap) throws ParseException {
        EntityResult er = new EntityResultMapImpl();
        er.setMessage("Not implemented yet");
        er.setCode(EntityResult.OPERATION_WRONG);
        return er;
    }

    @Override
    public EntityResult clienteUpdate(Map<String, Object> attrMap, Map<?, ?> keyMap) {
        EntityResult er = new EntityResultMapImpl();
        er.setMessage("Not implemented yet");
        er.setCode(EntityResult.OPERATION_WRONG);
        return er;
    }
}
