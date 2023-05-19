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

    public Hotel() {
    }

    public Hotel(String nombre, String direccion) {
        this.nombre = nombre;
        this.direccion = direccion;
    }
}
