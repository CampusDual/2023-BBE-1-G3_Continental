package com.hotel.Continental.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.Continental.api.IHabitacionService;
import com.hotel.Continental.api.IReservaService;
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
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class ReservaControllerTest {
    private MockMvc mockMvc;
    @Mock
    private IReservaService reservaService;
    @InjectMocks
    ReservaController reservaController;

    @BeforeEach
    public void setUp() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(reservaController).build();
    }

    @Test
    public void testAddReserva() throws Exception {
        ReservaDto reservaDto = new ReservaDto();
        reservaDto.setIdHabitacion(1);
        reservaDto.setFechaInicio(Date.valueOf("2021-06-01"));
        reservaDto.setFechaFin(Date.valueOf("2021-06-02"));
        reservaDto.setDniCliente("12345678A");
        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(reservaDto);
        MvcResult result = mockMvc.perform(post("/reserva/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isOk())
                .andReturn();
        String resultContent = result.getResponse().getContentAsString();
        assertEquals("0", resultContent);
    }

    @Test
    public void testAddReservaNull() throws Exception {
        ReservaDto reservaDto = new ReservaDto();
        reservaDto.setIdHabitacion(null);
        reservaDto.setFechaInicio(null);
        reservaDto.setFechaFin(null);
        reservaDto.setDniCliente(null);
        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = mapper.writeValueAsString(reservaDto);
        mockMvc.perform(post("/reserva/add")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonRequest))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testAddReservaNothing() throws Exception {
        mockMvc.perform(post("/reserva/add"))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testDeleteReserva() throws Exception {
        ReservaDto reservaDto = new ReservaDto();
        reservaDto.setFechaInicio(Date.valueOf("2021-06-01"));
        reservaDto.setFechaFin(Date.valueOf("2021-06-02"));
        reservaDto.setIdReserva(2);

        when(reservaService.deleteReserva(reservaDto)).thenReturn(reservaDto.getIdReserva());

        int result = reservaService.deleteReserva(reservaDto);
        assertEquals(2, result);
    }
}
