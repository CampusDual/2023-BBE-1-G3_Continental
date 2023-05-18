package com.hotel.Continental.Model.dao;

import org.springframework.data.jpa.repository.JpaRepository;

public interface HabitacionDao<Habitacion> extends JpaRepository<Habitacion, Integer>  {
}
