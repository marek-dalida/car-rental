package com.carrental.exception;

public class CarNotFoundException extends RuntimeException {
    public CarNotFoundException(Long id) {
        super("Not found car with id: %d".formatted(id));
    }
}
