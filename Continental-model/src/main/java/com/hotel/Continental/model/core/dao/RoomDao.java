package com.hotel.Continental.model.core.dao;

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
    public static final String IDHABITACION = "idhabitacion";
    public static final String IDHOTEL = "idhotel";
    public static final String ROOMNUMBER = "numhabitacion";
}

