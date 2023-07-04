package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "RefrigeratorsDao")
@ConfigurationFile(
        configurationFile = "dao/RefrigeratorsDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class RefrigeratorsDao extends OntimizeJdbcDaoSupport {
    public static final String FRIDGE_ID = "fridge_id";
    public static final String ROOM_ID = "room_id";
    public static final String CAPACITY = "capacity";
}
