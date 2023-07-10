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
    public static final String ID = "id";
    public static final String ID_PARKING = "id_parking";
    public static final String ID_BOOKING = "id_booking";
    public static final String ENTRY_DATE = "entry_date";
    public static final String EXIT_DATE = "exit_date";
}