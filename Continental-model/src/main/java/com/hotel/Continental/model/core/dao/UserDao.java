package com.hotel.continental.model.core.dao;


import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;


@Lazy
@Repository(value = "UserDao")
@ConfigurationFile(
        configurationFile = "dao/UserDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class UserDao extends OntimizeJdbcDaoSupport {

    public static final String USER = "user_";
    public static final String NIF = "nif";
    public static final String PASSWORD = "password";

    public static final String NAME = "name";
    public static final String SURNAME = "surname";
    public static final String USERBLOCKED = "user_blocked";
    public static final String COUNTRY_CODE = "country_code";
    public static final String LAST_PASSWORD_UPDATE = "last_password_update";


}
