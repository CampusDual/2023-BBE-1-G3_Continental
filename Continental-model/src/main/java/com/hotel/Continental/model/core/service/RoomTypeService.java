package com.hotel.continental.model.core.service;

import com.hotel.continental.api.core.service.IRoomService;
import com.hotel.continental.api.core.service.IRoomTypeService;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

@Lazy
@Service("RoomTypeService")
public class RoomTypeService implements IRoomTypeService {
}
