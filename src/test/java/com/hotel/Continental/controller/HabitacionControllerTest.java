package com.hotel.Continental.controller;

import com.hotel.Continental.api.IHabitacionService;
import com.hotel.Continental.model.dto.HabitacionDto;
import com.hotel.Continental.model.dto.ReservaDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.sql.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
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
        habitacionDto.setIdHotel(19);
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
        mockMvc.perform(post("/habitacion/add"))
                .andExpect(status().is(400));
    }

    @Test
    public void testGetHabitacionesLibres() throws Exception {
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

    @Test
    public void testGetHabitacionIDExiste() throws Exception {
        HabitacionDto habitacionDto = new HabitacionDto();
        habitacionDto.setNumHabitacion(103);
        habitacionDto.setIdHabitacion(1);
        habitacionDto.setIdHotel(19);
        when(ihabitacionService.getHabitacionById(1)).thenReturn(habitacionDto);
        HabitacionDto habitacionDto1 = ihabitacionService.getHabitacionById(1);
        MvcResult result = mockMvc.perform(get("/habitacion/getHabitacionById/1"))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = result.getResponse().getContentAsString();
        assertEquals(habitacionDto.getIdHotel(), habitacionDto1.getIdHotel());
        assertEquals(habitacionDto.getNumHabitacion(), habitacionDto1.getNumHabitacion());
        assertEquals(habitacionDto.getIdHabitacion(), habitacionDto1.getIdHabitacion());
        assertEquals(resultContent, "{\"idHabitacion\":1,\"idHotel\":19,\"numHabitacion\":103}");
    }

    @Test
    public void testGetHabitacionIDNoExiste() throws Exception {
        when(ihabitacionService.getHabitacionById(1)).thenReturn(null);
        HabitacionDto habitacionDto1 = ihabitacionService.getHabitacionById(2);
        MvcResult result = mockMvc.perform(get("/habitacion/getHabitacionById/2"))
                .andExpect(status().isNotFound())
                .andReturn();
        assertEquals(null, habitacionDto1);
    }

    @Test
    public void testGetHabitacionNothing() throws Exception {
        mockMvc.perform(get("/habitacion/getHabitacionById"))
                .andExpect(status().isNotFound());
    }
    @Test
    public void testEliminarHabitacion() throws Exception {
        HabitacionDto habitacionDto = new HabitacionDto();
        habitacionDto.setIdHabitacion(0);
        habitacionDto.setIdHotel(19);
        habitacionDto.setNumHabitacion(103);
        when(ihabitacionService.getHabitacionById(anyInt())).thenReturn(habitacionDto);
        when(ihabitacionService.deleteHabitacion(any(HabitacionDto.class))).thenReturn(1);
        MvcResult mvcResult=mockMvc.perform(delete("/habitacion/delete")
                        .content("{\"idHabitacion\":1}")
                        .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andReturn();
        assertEquals("1", mvcResult.getResponse().getContentAsString());
    }
    @Test
    public void testEliminarHabitacionNoExiste() throws Exception {
        HabitacionDto habitacionDto = new HabitacionDto();
        habitacionDto.setIdHabitacion(0);
        habitacionDto.setIdHotel(19);
        habitacionDto.setNumHabitacion(103);
        when(ihabitacionService.getHabitacionById(anyInt())).thenReturn(null);
        when(ihabitacionService.deleteHabitacion(any(HabitacionDto.class))).thenReturn(1);
        MvcResult mvcResult=mockMvc.perform(delete("/habitacion/delete")
                        .content("{\"idHabitacion\":1}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andReturn();
    }
    @Test
    public void testEliminarHabitacionNothing() throws Exception {
        HabitacionDto habitacionDto = new HabitacionDto();
        habitacionDto.setIdHabitacion(0);
        habitacionDto.setIdHotel(19);
        habitacionDto.setNumHabitacion(103);
        when(ihabitacionService.getHabitacionById(anyInt())).thenReturn(null);
        when(ihabitacionService.deleteHabitacion(any(HabitacionDto.class))).thenReturn(1);
        MvcResult mvcResult=mockMvc.perform(delete("/habitacion/delete")
                        .content("")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();
    }
}
