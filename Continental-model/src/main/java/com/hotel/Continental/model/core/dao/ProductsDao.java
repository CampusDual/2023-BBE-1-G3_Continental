package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "ProductsDao")
@ConfigurationFile(
        configurationFile = "dao/ProductsDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class ProductsDao extends OntimizeJdbcDaoSupport {
    public static final String PRODUCT_ID = "product_id";
    public static final String NAME = "name";
    public static final String PRICE = "price";
}
