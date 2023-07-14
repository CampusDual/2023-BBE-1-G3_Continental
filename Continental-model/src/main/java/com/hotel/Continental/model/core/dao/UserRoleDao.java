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
    public static final String ID_USER_ROLE = "id_user_role";
    public static final String ID_ROLENAME = "id_rolename";
    public static final String USER = "user_";
}
