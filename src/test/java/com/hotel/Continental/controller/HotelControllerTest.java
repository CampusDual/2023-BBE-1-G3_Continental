package com.hotel.Continental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dto.HotelDTO;
import com.hotel.Continental.service.HotelService;
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

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class HotelControllerTest {
	private MockMvc mockMvc;
	@InjectMocks
	HotelController hotelController;

	@BeforeEach
	public void setup() {
		mockMvc = MockMvcBuilders.standaloneSetup(hotelController)
				.build();
	}

	@Test
	public void addHotelTest() throws Exception {
		MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/hotel/add").contentType(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(new Hotel("Hotel1", "direccion1"))))
				.andReturn();

		assertEquals(200, mvcResult.getResponse().getStatus());
	}

	@Test
	public void testInsertNull() {
		HotelDTO hotelDTO = new HotelDTO();
		hotelDTO.setName(null);
		hotelDTO.setAddress(null);
		assertThrows(NullPointerException.class, () -> {
			hotelController.addHotel(hotelDTO);
		});
	}
}
