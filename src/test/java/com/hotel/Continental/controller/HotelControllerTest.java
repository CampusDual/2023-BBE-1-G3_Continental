package com.hotel.Continental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.Continental.api.IHotelService;
import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dto.HotelDTO;
import com.hotel.Continental.service.HotelService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
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
    @Mock
    private IHotelService ihotelService;
    @InjectMocks
    HotelController hotelController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(hotelController).build();
    }

    @Test
    public void addHotelTest() throws Exception {
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setName("Continental");
        hotelDTO.setAddress("123 Main St");
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(hotelDTO);
        MvcResult result = mockMvc.perform(post("/hotel/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = result.getResponse().getContentAsString();
        assertEquals("0", resultContent);
    }

    @Test
    public void testInsertNull() throws Exception {
        HotelDTO hotelDTO = new HotelDTO();
        hotelDTO.setName(null);
        hotelDTO.setAddress(null);
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonRequest = objectMapper.writeValueAsString(hotelDTO);
        MvcResult result = mockMvc.perform(post("/hotel/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest())
                .andReturn();

    }

    @Test
    public void testInsertNothing() throws Exception {
        mockMvc.perform(post("/hotel/add")).andExpect(status().isBadRequest());
    }
}
