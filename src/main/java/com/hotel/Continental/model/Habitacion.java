package com.hotel.Continental.model;

import javax.persistence.*;

@Entity
@Table(name="habitaciones")
public class Habitacion {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idHabitacion;
    @ManyToOne
    @JoinColumn(name = "idHotel")
    private Hotel hotel;
    @Column
    private int numHabitacion;

    public Habitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }

    public Habitacion() {
    }

    public int getIdHabitacion() {
        return idHabitacion;
    }

    public Habitacion setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
        return this;
    }

    public Hotel getHotel() {
        return hotel;
    }

    public Habitacion setHotel(Hotel hotel) {
        this.hotel = hotel;
        return this;
    }

    public int getNumHabitacion() {
        return numHabitacion;
    }

    public Habitacion setNumHabitacion(int numHabitacion) {
        this.numHabitacion = numHabitacion;
        return this;
    }
}
