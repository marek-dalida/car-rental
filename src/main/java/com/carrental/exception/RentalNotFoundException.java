package com.carrental.exception;

public class RentalNotFoundException extends RuntimeException {
    public RentalNotFoundException(Long rentalId) {
        super("Not found rental with id: %d".formatted(rentalId));
    }
}
