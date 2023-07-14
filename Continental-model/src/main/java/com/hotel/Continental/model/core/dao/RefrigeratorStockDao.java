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
    public static final String STOCK_ID = "stock_id";
    public static final String FRIDGE_ID = "fridge_id";
    public static final String PRODUCT_ID = "product_id";
    public static final String STOCK = "stock";
}
