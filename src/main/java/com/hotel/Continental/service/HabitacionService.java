package com.hotel.Continental.service;

import com.hotel.Continental.Model.Dto.HabitacionDto;
import com.hotel.Continental.api.IHabitacionService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Service("HabitacionService")
@Lazy
public class HabitacionService implements IHabitacionService {
    @Override
    public int insertHabitacion(HabitacionDto habitacionDto) {
        return 0;
    }
}
