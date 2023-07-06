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
    public static final String EMPLOYEEID = "employeeid";
    public static final String TUSER_NAME = "tuser_name";

    public static final String NAME = "name";
    public static final String DOCUMENT = "document";
    public static final String IDHOTEL = "hotelid";
    public static final String EMPLOYEEDOWNDATE = "employeedowndate";
    public static final String EMPLOYEE_INFO = "employee_info";

}
