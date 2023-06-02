package com.hotel.Continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "BookDao")
@ConfigurationFile(
        configurationFile = "dao/BookingDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class BookingDao extends OntimizeJdbcDaoSupport {
    public static final String BOOKINGID = "idreserva";
    public static final String ROOMID = "idhabitacion";
    public static final String STARTDATE = "fechainicio";
    public static final String ENDDATE = "fechafin";
    public static final String CLIENT = "idClient";
    public static final String QUERY_BOOKED_ROOMS = "current_bookings";
}
