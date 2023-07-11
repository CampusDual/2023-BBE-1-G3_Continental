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
    public static final String IDROOM = "roomid";
    public static final String IDHOTEL = "hotelid";
    public static final String ROOMNUMBER = "roomnumber";
    public static final String ROOMDOWNDATE = "roomdowndate";
    public static final String ROOMTYPEID = "typeroomid";

}

