package com.hotel.Continental.model.dto;

import java.sql.Date;

public class ReservaDto {
    private int idReserva;
    private Integer idHabitacion;
    private Date fechaInicio;
    private Date fechaFin;
    private String dniCliente;

    public Integer getIdHabitacion() {
        return idHabitacion;
    }
    public void setIdHabitacion(int idHotel) {
        this.idHabitacion = idHotel;
    }

    public int getIdReserva() {
        return idReserva;
    }

    public void setIdReserva(int idReserva) {
        this.idReserva = idReserva;
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

    public ReservaDto setIdHabitacion(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
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
