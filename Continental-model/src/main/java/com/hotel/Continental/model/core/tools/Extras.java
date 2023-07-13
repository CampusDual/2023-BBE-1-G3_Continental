package com.hotel.continental.model.core.tools;

import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;

public class Extras {
    /**
     * Creates an EntityResult with the given code and message
     *
     * @param code    EntityResult code
     * @param message EntityResult message
     * @return EntityResult
     */
    public static EntityResult createEntityResult(int code, String message) {
        EntityResult er = new EntityResultMapImpl();
        er.setCode(code);
        er.setMessage(message);
        return er;
    }
}
