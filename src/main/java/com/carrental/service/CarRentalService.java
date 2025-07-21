package com.carrental.service;

import com.carrental.exception.CarNotFoundException;
import com.carrental.exception.CarUnavailableException;
import com.carrental.exception.ClientNotFoundException;
import com.carrental.model.Car;
import com.carrental.model.CarRent;
import com.carrental.model.CarType;
import com.carrental.model.RentalClient;
import com.carrental.repository.CarRentRepository;
import com.carrental.repository.CarRepository;
import com.carrental.repository.RentalClientRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@AllArgsConstructor
public class CarRentalService {

    private final CarRentRepository carRentRepository;
    private final CarRepository carRepository;
    private final RentalClientRepository rentalClientRepository;

    public CarRent rentCar(Long carId, Long clientId, LocalDate rentStart, Integer rentDays) {
        Car car = getCarById(carId);
        RentalClient client = getRentalClientById(clientId);

        var rentEnd = rentStart.plusDays(rentDays);
        var overlappingCarRent = carRentRepository.findOverlappingCarRent(carId, rentStart, rentEnd);
        if (!overlappingCarRent.isEmpty()) {
            throw new CarUnavailableException(carId);
        }

        var carRent = new CarRent(car, client, rentStart, rentDays);
        return carRentRepository.save(carRent);
    }

    public Car addCar(String name, CarType carType) {
        var car = new Car(name, carType);
        return carRepository.save(car);
    }

    public RentalClient addRentalClient(String name) {
        var client = new RentalClient(name);
        return rentalClientRepository.save(client);
    }

    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() ->  new CarNotFoundException(id));
    }

    public RentalClient getRentalClientById(Long id) {
        return rentalClientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }
}
