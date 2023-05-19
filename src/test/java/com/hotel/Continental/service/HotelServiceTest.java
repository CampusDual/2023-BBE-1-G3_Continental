package com.hotel.Continental.service;

import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dao.HotelDao;
import com.hotel.Continental.model.dto.HotelDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
class HotelServiceTest {

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
		hotelDTO.setNombre("Hotel ABC");
		hotelDTO.setDireccion("123 Main Street");
		Hotel hotel = new Hotel();
		hotel.setNombre("Hotel ABC");
		hotel.setDireccion("123 Main Street");
		when(hotelDao.saveAndFlush(any(Hotel.class))).thenReturn(hotel);
		int result = hotelService.insertHotel(hotelDTO);
		assertEquals(0, result);
	}
}
