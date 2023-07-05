package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "RefrigeratorStockDao")
@ConfigurationFile(
        configurationFile = "dao/RefrigeratorStockDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class RefrigeratorStockDao extends OntimizeJdbcDaoSupport {
    public static final String STOCKID = "stock_id";
    public static final String FRIDGEID = "fridge_id";
    public static final String QUANTITY = "quantity";
}
