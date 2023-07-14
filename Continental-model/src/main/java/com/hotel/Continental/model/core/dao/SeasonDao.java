package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "SeasonDao")
@ConfigurationFile(
        configurationFile = "dao/SeasonDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class SeasonDao extends OntimizeJdbcDaoSupport {
    public static final String SEASON_ID = "season_id";
    public static final String CRITERIA_ID = "criteria_id";
    public static final String START_DAY = "start_day";
    public static final String START_MONTH = "start_month";
    public static final String END_DAY = "end_day";
    public static final String END_MONTH = "end_month";
    public static final String GET_SEASONS= "getSeason";

}
