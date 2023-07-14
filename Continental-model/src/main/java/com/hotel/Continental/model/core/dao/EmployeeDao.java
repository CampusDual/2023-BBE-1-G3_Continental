package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "EmployeeDao")
@ConfigurationFile(
        configurationFile = "dao/EmployeeDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class EmployeeDao extends OntimizeJdbcDaoSupport {
    public static final String EMPLOYEE_ID = "employee_id";
    public static final String NAME = "name";
    public static final String DOCUMENT = "document";
    public static final String HOTEL_ID = "hotel_id";
    public static final String EMPLOYEE_DOWN_DATE = "employee_down_date";

}
