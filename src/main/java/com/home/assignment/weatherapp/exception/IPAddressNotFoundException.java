package com.home.assignment.weatherapp.exception;

public class IPAddressNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IPAddressNotFoundException() {
        super();
    }

    public IPAddressNotFoundException(final String message) {
        super(message);
    }

}
