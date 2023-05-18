package com.hotel.Continental.model;

import javax.persistence.*;

@Entity
@Table(name="Habitacion")
public class Habitacion {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idHabitacion;
    @ManyToOne
    @JoinColumn(name = "idHotel")
    private Hotel hotel;
    @Column
    private int numHabitacion;
    public int getIdHabitacion() {
        return idHabitacion;
    }
    public void setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }
    public Hotel getIdHotel() {
        return hotel;
    }
    public void setIdHotel(Hotel hotel) {
        this.hotel = hotel;
    }
    public int getNumHabitacion() {
        return numHabitacion;
    }
    public void setNumHabitacion(int numHabitacion) {
        this.numHabitacion = numHabitacion;
    }
}
