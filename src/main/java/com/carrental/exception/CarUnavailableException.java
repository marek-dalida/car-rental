package com.carrental.exception;

public class CarUnavailableException extends RuntimeException {
    public CarUnavailableException(Long carId) {
        super("Car with given id: %d is not available for rent for given period".formatted(carId));
    }
}
