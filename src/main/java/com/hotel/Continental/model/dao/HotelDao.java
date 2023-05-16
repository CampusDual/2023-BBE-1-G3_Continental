package com.hotel.Continental.model.dao;

import com.hotel.Continental.model.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
public interface HotelDao extends JpaRepository<Hotel,Integer> {}
