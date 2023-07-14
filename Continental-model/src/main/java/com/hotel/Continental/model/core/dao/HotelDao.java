package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "HotelDao")
@ConfigurationFile(
        configurationFile = "dao/HotelDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class HotelDao extends OntimizeJdbcDaoSupport {
    public static final String HOTEL_ID = "hotel_id";
    public static final String ADDRESS = "address";
    public static final String NAME = "name";
    public static final String HOTELDOWNDATE = "hoteldowndate";
}
