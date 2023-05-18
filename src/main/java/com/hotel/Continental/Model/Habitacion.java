package com.hotel.Continental.Model;


import jakarta.persistence.*;

@Entity
@Table(name="Habitacion")
public class Habitacion {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private int idHabitacion;
    @Column
    private int idHotel;
    @Column
    private int numHabitacion;
    public int getIdHabitacion() {
        return idHabitacion;
    }
    public void setIdHabitacion(int idHabitacion) {
        this.idHabitacion = idHabitacion;
    }
    public int getIdHotel() {
        return idHotel;
    }
    public void setIdHotel(int idHotel) {
        this.idHotel = idHotel;
    }
    public int getNumHabitacion() {
        return numHabitacion;
    }
    public void setNumHabitacion(int numHabitacion) {
        this.numHabitacion = numHabitacion;
    }
}
