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
    public static final String BOOKINGID = "booking_id";
    public static final String ROOMID = "room_id";
    public static final String STARTDATE = "initial_date";
    public static final String ENDDATE = "end_date";
    public static final String CLIENT = "client_id";
    public static final String CHECKIN_DATETIME = "checkin_datetime";
    public static final String CHECKOUT_DATETIME = "checkout_datetime";
    public static final String PRICE = "price";
    public static final String QUERY_BOOKED_ROOMS = "current_bookings";
}
