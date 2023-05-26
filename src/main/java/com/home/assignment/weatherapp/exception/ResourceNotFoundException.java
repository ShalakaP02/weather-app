package com.home.assignment.weatherapp.exception;

public class ResourceNotFoundException  extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(final String message) {
        super(message);
    }

}

