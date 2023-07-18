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
    public static final String ACCESS_CARD_ASSIGNMENT_ID = "access_card_assignment_id";
    public static final String ACCESS_CARD_ID = "access_card_id";
    public static final String BOOKING_ID = "booking_id";
}
