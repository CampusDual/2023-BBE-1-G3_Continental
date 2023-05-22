package com.hotel.Continental.controller;

import com.hotel.Continental.api.IHabitacionService;
import com.hotel.Continental.api.IHotelService;
import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.HotelDTO;
import com.hotel.Continental.model.dto.ReservaDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;
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
        mockMvc.perform(post("/habitacion/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idHotel\":19,\"numHabitacion\":103}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testInsertNull() throws Exception {
        mockMvc.perform(post("/habitacion/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"idHotel\":null,\"numHabitacion\":null}"))
                .andExpect(status().is(400));
    }

    @Test
    public void testInsertNothing() throws Exception {
        mockMvc.perform(post("/habitacion/add"))
                .andExpect(status().is(400));
    }

    @Test
    public void testGetHabitacionesLibres() throws Exception {
        //HabitacionDto habitacionDto = new HabitacionDto();
        ReservaDto reservaDto = new ReservaDto();
        reservaDto.setFechaInicio(Date.valueOf("2021-06-01"));
        reservaDto.setFechaFin(Date.valueOf("2021-06-02"));

        HabitacionDto habitacionDto = new HabitacionDto();
        habitacionDto.setIdHotel(19);
        habitacionDto.setNumHabitacion(103);

        when(ihabitacionService.getHabitacionesLibres(reservaDto.getFechaInicio(), reservaDto.getFechaFin()))
                .thenReturn(List.of(habitacionDto));

        List<HabitacionDto> habitacionDtoList = ihabitacionService.getHabitacionesLibres(reservaDto.getFechaInicio(), reservaDto.getFechaFin());

        MvcResult result = mockMvc.perform(post("/habitacion/getHabitacionesLibres")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fechaInicio\":\"2021-06-01\",\"fechaFin\":\"2021-06-02\"}"))
                .andExpect(status().isOk())
                .andReturn();

        String resultContent = result.getResponse().getContentAsString();
        assertEquals(habitacionDtoList.size(), 1);

    }

}

