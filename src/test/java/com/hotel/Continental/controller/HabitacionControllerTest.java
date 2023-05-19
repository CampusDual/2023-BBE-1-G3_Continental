package com.hotel.Continental.controller;

import com.hotel.Continental.api.IHabitacionService;
import com.hotel.Continental.api.IHotelService;
import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.HotelDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class HabitacionControllerTest {

    private MockMvc mockMvc;
    @Mock
    private IHabitacionService ihabitacionService;
    @InjectMocks
    HabitacionController habitacionController;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitacionController)
                .build();
    }
    @Test
    public void testInsertHabitación() throws Exception {
        HabitacionDto habitacionDto = new HabitacionDto();
        habitacionDto.setIdHotel(new Hotel(19));
        habitacionDto.setNumHabitacion(103);
        mockMvc.perform(post("/habitacion/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idHotel\":19,\"numHabitacion\":103}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testInsertNull() throws Exception {
        HabitacionDto habitacionDto = new HabitacionDto();
        habitacionDto.setIdHotel(null);
        habitacionDto.setNumHabitacion(null);
        mockMvc.perform(post("/habitacion/add")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"idHotel\":null,\"numHabitacion\":null}"))
                .andExpect(status().is(400));
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
