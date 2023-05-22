package com.hotel.Continental.model.dao;

import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Date;
import java.util.List;

public interface ReservaDao extends JpaRepository<Reserva, Integer> {

}
