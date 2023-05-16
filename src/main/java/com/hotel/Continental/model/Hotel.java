package com.hotel.Continental.model;


import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "hotels")
public class Hotel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column
    @NotNull
    private String name;
    @Column
    @NotNull
    private String address;

    public int getId() {
        return id;
    }

    public Hotel setId(int id) {
        this.id = id;
        return this;
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
