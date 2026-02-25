package com.carrental.service;

import com.carrental.enums.CarStatus;
import com.carrental.enums.RentalStatus;
import com.carrental.exception.CarNotFoundException;
import com.carrental.exception.CarUnavailableException;
import com.carrental.exception.ClientNotFoundException;
import com.carrental.model.Car;
import com.carrental.model.CarRental;
import com.carrental.enums.CarType;
import com.carrental.model.RentalClient;
import com.carrental.repository.CarRentalRepository;
import com.carrental.repository.CarRepository;
import com.carrental.repository.RentalClientRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

@Service
@AllArgsConstructor
@Slf4j
public class CarRentalService {

    private final CarRentalRepository carRentalRepository;
    private final CarRepository carRepository;
    private final RentalClientRepository rentalClientRepository;
    private final EntityManager entityManager;

    @Transactional
    public CarRental rentCar(Long carId, Long clientId, LocalDate rentStart, Integer rentDays) {
        Car car = carRepository.findById(carId)
                .orElseThrow(() ->  new CarNotFoundException(carId));

        if (car.getStatus() != CarStatus.AVAILABLE) {
            throw new CarUnavailableException("Car is not available carId: %s current status: %s".formatted(carId, car.getStatus()));
        }
        RentalClient client = getRentalClientById(clientId);

        var rentEnd = rentStart.plusDays(rentDays);
        var overlappingCarRent = carRentalRepository.findOverlappingCarRent(carId, rentStart, rentEnd);
        if (!overlappingCarRent.isEmpty()) {
            throw new CarUnavailableException(carId);
        }

        var carRental = new CarRental(car, client, rentStart, rentDays, RentalStatus.CONFIRMED);
        car.setStatus(CarStatus.RENTED);
        try {
            carRentalRepository.save(carRental);
            carRepository.save(car);
            entityManager.flush();
            log.info("Rental created successfully: {}", carRental.getId());
            return carRental;
        } catch (OptimisticLockException ex) {
            log.error("Optimistic lock exception during rental cancellation", ex);
            throw ex;
        }
    }

    public Car addCar(String brand, String model, CarType carType) {
        var car = new Car(brand, model, carType);
        return carRepository.save(car);
    }

    public RentalClient addRentalClient(String name) {
        var client = new RentalClient(name);
        return rentalClientRepository.save(client);
    }

    @Transactional(readOnly = true)
    public Car getCarById(Long id) {
        return carRepository.findById(id)
                .orElseThrow(() ->  new CarNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public RentalClient getRentalClientById(Long id) {
        return rentalClientRepository.findById(id)
                .orElseThrow(() -> new ClientNotFoundException(id));
    }
}
