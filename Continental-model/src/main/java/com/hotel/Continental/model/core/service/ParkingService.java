package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IParkingService;
import com.hotel.continental.model.core.dao.BookingDao;
import com.hotel.continental.model.core.dao.ParkingDao;
import com.hotel.continental.model.core.dao.ParkingHistoryDao;
import com.hotel.continental.model.core.tools.ErrorMessages;
import com.ontimize.jee.common.dto.EntityResult;
import com.ontimize.jee.common.dto.EntityResultMapImpl;
import com.ontimize.jee.server.dao.DefaultOntimizeDaoHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;

@Lazy
@Service("ParkingService")
public class ParkingService implements IParkingService {

    @Autowired
    private DefaultOntimizeDaoHelper daoHelper;
    @Autowired
    private ParkingDao parkingDao;


    @Override
    public EntityResult parkingEnter(Map<?, ?> attrMap) {
        EntityResult er = new EntityResultMapImpl();
        //Comprobar que me llega los datos necesarios para hacer la entrada id_booking id_parking
        if(attrMap.get("id_booking") == null || attrMap.get("id_parking") == null){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.NECESSARY_DATA);
            return er;
        }
        //Comprobar que hay sitio en el parking indicado
        Map<String, Object> attrMapParking = Map.of(ParkingDao.ID_PARKING, attrMap.get(ParkingDao.ID_PARKING));
        EntityResult erParking = this.daoHelper.query(parkingDao, attrMap, List.of(ParkingDao.OCCUPIED_CAPACITY,ParkingDao.TOTAL_CAPACITY));
        if(erParking.calculateRecordNumber() == 0){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.PARKING_NOT_FOUND);
            return er;
        }
        if((int)erParking.getRecordValues(0).get(ParkingDao.OCCUPIED_CAPACITY) == (int)erParking.getRecordValues(0).get(ParkingDao.TOTAL_CAPACITY)){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.PARKING_FULL);
            return er;
        }
        //Comprobar que la reserva existe
        Map<String, Object> attrMapBooking = Map.of(ParkingHistoryDao.ID_BOOKING, attrMap.get(ParkingHistoryDao.ID_BOOKING));
        EntityResult erBooking = this.daoHelper.query(parkingDao, attrMap, List.of(ParkingHistoryDao.ID_BOOKING, BookingDao.STARTDATE, BookingDao.ENDDATE, BookingDao.CHECKIN_DATETIME, BookingDao.CHECKOUT_DATETIME));
        if(erBooking.calculateRecordNumber() == 0){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_EXIST);
            return er;
        }
        //Comprobar que la reserva esta activa,que hizo checkin y que no ha hecho checkout
        if(erBooking.getRecordValues(0).get(BookingDao.CHECKIN_DATETIME) == null){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_CHECKED_IN);
            return er;
        }
        if(erBooking.getRecordValues(0).get(BookingDao.CHECKOUT_DATETIME) != null){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_ALREADY_CHECKED_OUT);
            return er;
        }
        //Comprobar que la fecha actual es igual o superior a la fecha de inicio de la reserva
        Date startDate = (Date) erBooking.getRecordValues(0).get(BookingDao.STARTDATE);
        Date currentDate = new Date();
        if(startDate.compareTo(currentDate) > 0){
            er.setCode(EntityResult.OPERATION_WRONG);
            er.setMessage(ErrorMessages.BOOKING_NOT_STARTED);
            return er;
        }

        //Comprobar que la reserva no ha entrado ya en el parking
        Map<String, Object> attrMapParkingHistory = Map.of(ParkingHistoryDao.ID_BOOKING, attrMap.get(ParkingHistoryDao.ID_BOOKING));
        
        //Insertar en la tabla parking_history
        //Actualizar tabla parking para sumar 1 a los coches que hay en el parking
        return null;
    }
}
