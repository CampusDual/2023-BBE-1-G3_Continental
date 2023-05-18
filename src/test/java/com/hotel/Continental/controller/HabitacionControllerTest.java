package com.hotel.Continental.controller;

import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.HotelDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class HabitacionControllerTest {

    private MockMvc mockMvc;
    @InjectMocks
    HabitacionController habitacionController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitacionController)
                .build();
    }

    @Test
    public void testInsertNothing() throws Exception {
        HabitacionDto habitacionDto = new HabitacionDto();
        habitacionDto.setIdHotel(null);
        habitacionDto.setNumHabitacion(null);
        mockMvc.perform(post("/habitacion/add"))
                .andExpect(status().is(400));
    }
}
