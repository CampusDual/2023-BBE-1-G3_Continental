package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "AccessCardDao")
@ConfigurationFile(
        configurationFile = "dao/AccessCardDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class AccessCardDao extends OntimizeJdbcDaoSupport {
    public static final String ACCESSCARDID = "accesscardid";
    public static final String HOTELID = "hotelid";
    public static final String AVAILABLE = "available";
    public static final String CARDDOWNDATE = "carddowndate";
}
