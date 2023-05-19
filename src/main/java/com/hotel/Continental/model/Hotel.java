package com.hotel.Continental.model;


import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "hoteles")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String nombre;
    @Column
    private String direccion;
    @OneToMany(mappedBy = "hotel")
    private List<Habitacion> habitaciones;

    public int getId() {
        return id;
    }

    public Hotel(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public Hotel setNombre(String name) {
        this.nombre = name;
        return this;
    }

    public String getDireccion() {
        return direccion;
    }

    public Hotel setDireccion(String address) {
        this.direccion = address;
        return this;
    }

    public List<Habitacion> getHabitaciones() {
        return habitaciones;
    }

    public Hotel setHabitaciones(List<Habitacion> habitaciones) {
        this.habitaciones = habitaciones;
        return this;
    }

    public Hotel() {
    }

    public Hotel(String nombre, String direccion, List<Habitacion> habitaciones) {
        this.nombre = nombre;
        this.direccion = direccion;
        this.habitaciones = habitaciones;
    }
}
