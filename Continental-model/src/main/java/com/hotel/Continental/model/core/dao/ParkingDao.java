package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "ParkingDao")
@ConfigurationFile(
        configurationFile = "dao/ParkingDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class ParkingDao extends OntimizeJdbcDaoSupport {
    public static final String PARKING_ID = "parking_id";
    public static final String HOTEL_ID = "hotel_id";
    public static final String TOTAL_CAPACITY = "total_capacity";
    public static final String OCCUPIED_CAPACITY = "occupied_capacity";
    public static final String DESCRIPTION = "description";
    public static final String PRICE = "price";
    public static final String PARKING_DOWN_DATE = "parking_down_date";
}
