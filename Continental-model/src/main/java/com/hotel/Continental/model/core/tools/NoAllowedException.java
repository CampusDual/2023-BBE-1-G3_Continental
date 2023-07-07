package com.hotel.continental.model.core.tools;

public class NoAllowedException extends Exception {
    public NoAllowedException() {
        super("NOT_AUTHORIZED");
    }
}
