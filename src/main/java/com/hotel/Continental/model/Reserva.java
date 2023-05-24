package com.hotel.Continental.model;

import javax.persistence.*;
import java.sql.Date;

@Entity
@Table(name = "reservas")
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int idReserva;
    @ManyToOne
    @JoinColumn(name = "idHabitacion")
    private Habitacion habitacion;
    @Column
    private Date fechaInicio;
    @Column
    private Date fechaFin;
    @Column
    private String dniCliente;

    public Reserva() {
    }

    public Reserva(int idReserva, Habitacion habitacion, Date fechaInicio, Date fechaFin, String dniCliente) {
        this.idReserva = idReserva;
        this.habitacion = habitacion;
        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        this.dniCliente = dniCliente;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
    }

    public Habitacion getHabitacion() {
        return habitacion;
    }

    public void setHabitacion(Habitacion habitacion) {
        this.habitacion = habitacion;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public void setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public void setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public void setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
    }
}
