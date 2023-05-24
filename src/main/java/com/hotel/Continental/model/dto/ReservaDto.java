package com.hotel.Continental.model.dto;

import java.sql.Date;

public class ReservaDto {
    private int idReserva;
    private Integer idHabitacion;
    private Date fechaInicio;
    private Date fechaFin;
    private String dniCliente;

    public ReservaDto() {
    }

    public ReservaDto(int idReserva, Integer idHabitacion, Date fechaInicio, Date fechaFin, String dniCliente) {
        this.idReserva = idReserva;
        this.idHabitacion = idHabitacion;
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

    public Integer getIdHabitacion() {
        return idHabitacion;
    }

    public void setIdHabitacion(Integer idHabitacion) {
        this.idHabitacion = idHabitacion;
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
