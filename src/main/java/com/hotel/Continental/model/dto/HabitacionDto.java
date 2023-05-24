package com.hotel.Continental.model.dto;

public class HabitacionDto {
    private Integer idHabitacion;
    private Integer idHotel;
    private Integer numHabitacion;

    public HabitacionDto() {
    }

    public HabitacionDto(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public HabitacionDto(Integer idHabitacion, Integer idHotel, Integer numHabitacion) {
        this.idHabitacion = idHabitacion;
        this.idHotel = idHotel;
        this.numHabitacion = numHabitacion;
    }

    public Integer getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public Integer getIdHotel() {
        return idHotel;
    }

    public void setIdHotel(Integer idHotel) {
        this.idHotel = idHotel;
    }

    public Integer getNumHabitacion() {
        return numHabitacion;
    }

    public void setNumHabitacion(Integer numHabitacion) {
        this.numHabitacion = numHabitacion;
    }
}
