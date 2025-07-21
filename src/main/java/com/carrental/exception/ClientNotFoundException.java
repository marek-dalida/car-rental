package com.carrental.exception;

public class ClientNotFoundException extends RuntimeException {
    public ClientNotFoundException(Long id) {
        super("Not found rental client with id: %d".formatted(id));
    }
}
