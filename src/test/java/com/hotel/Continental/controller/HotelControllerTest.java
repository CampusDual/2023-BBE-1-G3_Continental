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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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
	/*
	@Test
	public void addHotelTest() throws Exception {
		String jsonRequest = "{\"name\": \"Continental\", \"address\": \"DireccionInventada\"}";
		mockMvc.perform(post("/hotel/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonRequest))
				.andExpect(status().isOk());
	}

	@Test
	public void testInsertNull() throws Exception {
		String jsonRequest = "{\"name\": null, \"address\": null}";
		mockMvc.perform(post("/hotel/add")
						.contentType(MediaType.APPLICATION_JSON)
						.content(jsonRequest))
				.andExpect(status().is(400));
	}
	*/
	@Test
	public void testInsertNothing() throws Exception {
		HotelDTO hotelDTO = new HotelDTO();
		hotelDTO.setName(null);
		hotelDTO.setAddress(null);
		mockMvc.perform(post("/hotel/add"))
				.andExpect(status().is(400));
	}
}
