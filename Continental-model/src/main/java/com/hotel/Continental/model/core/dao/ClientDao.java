package com.hotel.Continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "ClientDao")
@ConfigurationFile(
        configurationFile = "dao/ClientDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class ClientDao {
    public static final String CLIENTID = "idclient";
    public static final String DOCUMENT = "document";
    public static final String NAME = "name";
    public static final String COUNTRY = "country";
}
