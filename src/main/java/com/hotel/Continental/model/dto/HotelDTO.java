package com.hotel.Continental.model.dto;

public class HotelDTO {
    private String nombre;
    private String direccion;

    public HotelDTO() {
    }

    public HotelDTO(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
}
