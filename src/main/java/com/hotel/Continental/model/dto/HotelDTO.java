package com.hotel.Continental.model.dto;

public class HotelDTO {
    private String nombre;
    private String direccion;
    public String getNombre() {
        return nombre;
    }

    public HotelDTO setNombre(String nombre) {
        this.nombre = nombre;
        return this;
    }

    public String getDireccion() {
        return direccion;
    }

    public HotelDTO setDireccion(String direccion) {
        this.direccion = direccion;
        return this;
    }
}
