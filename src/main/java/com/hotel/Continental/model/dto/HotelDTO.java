package com.hotel.Continental.model.dto;

public class HotelDTO {
    private String name;
    private String address;
    public String getName() {
        return name;
    }

    public HotelDTO setName(String name) {
        this.name = name;
        return this;
    }

    public String getAddress() {
        return address;
    }

    public HotelDTO setAddress(String address) {
        this.address = address;
        return this;
    }
}
