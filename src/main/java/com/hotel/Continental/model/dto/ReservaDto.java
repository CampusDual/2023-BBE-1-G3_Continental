package com.hotel.Continental.model.dto;

import java.sql.Date;

public class ReservaDto {
    private int idReserva;
    private Integer idHabitacion;
    private Date fechaInicio;
    private Date fechaFin;
    private String dniCliente;

    public int getIdReserva() {
        return idReserva;
    }

    public ReservaDto setIdReserva(int idReserva) {
        this.idReserva = idReserva;
        return this;
    }

    public Integer getIdHabitacion() {
        return idHabitacion;
    }

    public ReservaDto setIdHabitacion(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
        return this;
    }

    public Date getFechaInicio() {
        return fechaInicio;
    }

    public ReservaDto setFechaInicio(Date fechaInicio) {
        this.fechaInicio = fechaInicio;
        return this;
    }

    public Date getFechaFin() {
        return fechaFin;
    }

    public ReservaDto setFechaFin(Date fechaFin) {
        this.fechaFin = fechaFin;
        return this;
    }

    public String getDniCliente() {
        return dniCliente;
    }

    public ReservaDto setDniCliente(String dniCliente) {
        this.dniCliente = dniCliente;
        return this;
    }
}
