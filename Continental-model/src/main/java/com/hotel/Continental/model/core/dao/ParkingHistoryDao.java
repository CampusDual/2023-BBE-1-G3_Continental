package com.hotel.continental.model.core.dao;

import com.ontimize.jee.server.dao.common.ConfigurationFile;
import com.ontimize.jee.server.dao.jdbc.OntimizeJdbcDaoSupport;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

@Lazy
@Repository(value = "ParkingHistory")
@ConfigurationFile(
        configurationFile = "dao/ParkingHistoryDao.xml",
        configurationFilePlaceholder = "dao/placeholders.properties")
public class ParkingHistoryDao extends OntimizeJdbcDaoSupport {
    public static final String PARKING_HISTORY_ID = "parking_history_id";
    public static final String PARKING_ID = "parking_id";
    public static final String BOOKING_ID = "booking_id";
    public static final String ENTRY_DATE = "entry_date";
    public static final String EXIT_DATE = "exit_date";
}