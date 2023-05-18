package com.hotel.Continental.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    private String name;
    @Column
    private String address;
    @OneToMany(mappedBy = "hotel")
    private List<Habitacion> habitaciones;

    public int getId() {
        return id;
    }

    public Hotel(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public Hotel setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public Hotel setAddress(String address) {
        this.address = address;
        return this;
    }

    public Hotel() {
    }

    public Hotel(String name, String address) {
        this.name = name;
        this.address = address;
    }
}
