package com.hotel.continental.model.core.dao;


import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;


@Repository(value = "UserRoleDao")
@Lazy
@ConfigurationFile(
        configurationFile = "dao/UserRoleDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class UserRoleDao extends OntimizeJdbcDaoSupport {
    public static final String id_user_role = "id_user_role";
    public static final String id_rolename = "id_rolename";
    public static final String USERNAME = "username";
}
