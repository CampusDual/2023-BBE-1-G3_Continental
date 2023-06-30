package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "RoomTypeDao")
@ConfigurationFile(
        configurationFile = "dao/RoomTypeDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class RoomTypeDao extends OntimizeJdbcDaoSupport {
    public static final String TYPEID = "typeid";
    public static final String TYPE = "type";
    public static final String PRICE = "price";
}
