package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "RoomDao")
@ConfigurationFile(
        configurationFile = "dao/RoomDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class RoomDao extends OntimizeJdbcDaoSupport {
    public static final String ROOM_ID = "room_id";
    public static final String HOTEL_ID = "hotel_id";
    public static final String ROOM_NUMBER = "room_number";
    public static final String ROOM_DOWN_DATE = "room_down_date";
    public static final String ROOM_TYPE_ID = "room_type_id";

}

