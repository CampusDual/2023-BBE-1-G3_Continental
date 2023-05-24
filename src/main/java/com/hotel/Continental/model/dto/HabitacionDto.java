package com.hotel.Continental.model.dto;

public class HabitacionDto {
    private Integer idHabitacion;
    private Integer idHotel;
    private Integer numHabitacion;

    public Integer getIdHabitacion() {
        return idHabitacion;
    }

    public HabitacionDto setIdHabitacion(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
        return this;
    }

    public Integer getIdHotel() {
        return idHotel;
    }

    public HabitacionDto setIdHotel(Integer idHotel) {
        this.idHotel = idHotel;
        return this;
    }

    public Integer getNumHabitacion() {
        return numHabitacion;
    }

    public HabitacionDto setNumHabitacion(Integer numHabitacion) {
        this.numHabitacion = numHabitacion;
        return this;
    }
}
