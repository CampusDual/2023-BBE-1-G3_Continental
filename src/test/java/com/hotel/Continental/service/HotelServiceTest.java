package com.hotel.Continental.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.Continental.controller.HotelController;
import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dao.HotelDao;
import com.hotel.Continental.model.dto.HotelDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class HotelServiceTest {

	@Test
	void contextLoads() {
	}
	private MockMvc mockMvc;
	@Mock
	HotelService hotelService;

	@Mock
	private HotelDao hotelDao;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(hotelService)
				.build();
	}
	@Test
	public void testInsertHotel() {
		HotelDTO hotelDTO = new HotelDTO();
		hotelDTO.setName("Hotel ABC");
		hotelDTO.setAddress("123 Main Street");
		Hotel hotel = new Hotel();
		hotel.setName("Hotel ABC");
		hotel.setAddress("123 Main Street");
		when(hotelDao.saveAndFlush(any(Hotel.class))).thenReturn(hotel);
		int result = hotelService.insertHotel(hotelDTO);
		assertEquals(0, result);
	}
}
