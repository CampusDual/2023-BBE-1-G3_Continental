package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "RoleDao")
@ConfigurationFile(
        configurationFile = "dao/RoleDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class RoleDao extends OntimizeJdbcDaoSupport {
    public static final String ROLE_ID = "role_id";
    public static final String ROLE_NAME = "role_name";
    public static final String XMLCLIENTPERMISSION = "xmlclientpermission";
}
