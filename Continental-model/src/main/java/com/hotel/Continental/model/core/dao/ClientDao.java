package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "ClientDao")
@ConfigurationFile(
        configurationFile = "dao/ClientDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class ClientDao extends OntimizeJdbcDaoSupport {
    public static final String CLIENT_ID = "client_id";
    public static final String DOCUMENT = "document";
    public static final String NAME = "name";
    public static final String COUNTRY_CODE = "country_code";
    public static final String CLIENT_DOWN_DATE = "client_down_date";
}
