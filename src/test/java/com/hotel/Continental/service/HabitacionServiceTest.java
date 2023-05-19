package com.hotel.Continental.service;

import com.hotel.Continental.model.Habitacion;
import com.hotel.Continental.model.Hotel;
import com.hotel.Continental.model.dao.HabitacionDao;
import com.hotel.Continental.model.dao.HotelDao;
import com.hotel.Continental.model.dto.HabitacionDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
public class HabitacionServiceTest {
    private MockMvc mockMvc;
    @Mock
    HabitacionService habitacionService;
    @Mock
    private HabitacionDao habitacionDao;

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(habitacionService).build();
    }

    @Test
    public void testInsertHotel() {
        HabitacionDto habitacionDto = new HabitacionDto();
        habitacionDto.setIdHotel(19);
        habitacionDto.setNumHabitacion(103);

        Habitacion habitacion = new Habitacion();
        habitacion.setIdHotel(new Hotel(19));
        habitacion.setNumHabitacion(103);

        when(habitacionDao.saveAndFlush(any(Habitacion.class))).thenReturn(habitacion);
        int result = habitacionService.insertHabitacion(habitacionDto);
        assertEquals(0, result);


    }
}
