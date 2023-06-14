package com.hotel.continental.model.core.dao;

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
    public static final String BOOKINGID = "bookingid";
    public static final String ROOMID = "roomid";
    public static final String STARTDATE = "initialdate";
    public static final String ENDDATE = "enddate";
    public static final String CLIENT = "clientid";
    public static final String QUERY_BOOKED_ROOMS = "current_bookings";
}
