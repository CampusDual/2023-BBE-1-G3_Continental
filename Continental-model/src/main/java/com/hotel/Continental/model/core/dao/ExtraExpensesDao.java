package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "ExtraExpensesDao")
@ConfigurationFile(
        configurationFile = "dao/ExtraExpensesDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class ExtraExpensesDao extends OntimizeJdbcDaoSupport {
    public static final String EXTRA_EXPENSE_ID = "extra_expense_id";
    public static final String BOOKING_ID = "booking_id";
    public static final String CONCEPT = "concept";
    public static final String PRICE = "price";
}
