package com.hotel.Continental.model.dto;

import java.sql.Date;

public class ReservaDto {
    private int idReserva;
    private Integer idHotel;
    private Date fechaInicio;
    private Date fechaFin;

    public Integer getIdHotel() {
        return idHotel;
    }
    public void setIdHotel(Integer idHotel) {
        this.idHotel= idHotel;
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
}
