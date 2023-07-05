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
    public static final String CLIENTID = "idclient";
    public static final String TUSER_NAME = "tuser_name";
    public static final String DOCUMENT = "document";
    public static final String NAME = "name";
    public static final String COUNTRYCODE = "countrycode";
    public static final String CLIENTDOWNDATE = "clientdowndate";

}
