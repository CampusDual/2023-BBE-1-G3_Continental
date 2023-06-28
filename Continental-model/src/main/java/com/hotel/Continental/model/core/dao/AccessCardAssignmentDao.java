package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "AccessCardAssignmentDao")
@ConfigurationFile(
        configurationFile = "dao/AccessCardAssignmentDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class AccessCardAssignmentDao extends OntimizeJdbcDaoSupport {
    public static final String ACCESSCARDASIGNMENT = "accesscardasignment";
    public static final String ACCESSCARDID = "accesscardid";
    public static final String BOOKINGID = "bookingid";
}
