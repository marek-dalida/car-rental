package com.carrental;

import com.carrental.enums.CarStatus;
import com.carrental.exception.CarNotFoundException;
import com.carrental.exception.CarUnavailableException;
import com.carrental.exception.ClientNotFoundException;
import com.carrental.model.Car;
import com.carrental.enums.CarType;
import com.carrental.model.CarRental;
import com.carrental.model.RentalClient;
import com.carrental.repository.CarRentalRepository;
import com.carrental.repository.CarRepository;
import com.carrental.repository.RentalClientRepository;
import com.carrental.service.CarRentalService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.OptimisticLockException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CarRentalApplicationTests {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private RentalClientRepository rentalClientRepository;

    @Autowired
    private CarRentalRepository carRentRepository;

    @Autowired
    private CarRentalService carRentalService;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @AfterEach
    void cleanup() {
        carRentRepository.deleteAll();
        carRepository.deleteAll();
        rentalClientRepository.deleteAll();
    }

    @Test
    void shouldAddCars() {
        var expectedCar1 = carRentalService.addCar("Toyota",  "Prius", CarType.SEDAN);
        var foundCar1 = carRepository.findById(expectedCar1.getId()).orElseThrow();
        assertNotNull(foundCar1);
        assertEquals(expectedCar1, foundCar1);

        var expectedCar2 = carRentalService.addCar("Ford", "Transit", CarType.VAN);
        var foundCar2 = carRepository.findById(expectedCar2.getId()).orElseThrow();
        assertNotNull(foundCar2);
        assertEquals(expectedCar2, foundCar2);

        var expectedCar3 = carRentalService.addCar("Ford", "Expolorer", CarType.SUV);
        var foundCar3 = carRepository.findById(expectedCar3.getId()).orElseThrow();
        assertNotNull(foundCar3);
        assertEquals(expectedCar3, foundCar3);
    }

    @Test
    void shouldAddRentalClients() {
        var expectedClient1 = carRentalService.addRentalClient("John");
        var foundClient1 = rentalClientRepository.findById(expectedClient1.getId()).orElseThrow();
        assertNotNull(foundClient1);
        assertEquals(expectedClient1, foundClient1);

        var expectedClient2 = carRentalService.addRentalClient("Mike");
        var foundClient2 = rentalClientRepository.findById(expectedClient2.getId()).orElseThrow();
        assertNotNull(foundClient2);
        assertEquals(expectedClient2, foundClient2);
    }

    @Test
    void shouldGetCarById() {
        var car = addMockCar("Honda", "CR-V", CarType.SUV);
        var foundCar = carRentalService.getCarById(car.getId());
        assertNotNull(car);
        assertEquals(car, foundCar);
    }

    @Test
    void shouldNotFoundCar() {
        assertThrows(CarNotFoundException.class, ()  -> carRentalService.getCarById(999L));
    }

    @Test
    void shouldGetRentalClientById() {
        var client = addMockRentalClient("Howard");
        var foundClient = carRentalService.getRentalClientById(client.getId());
        assertNotNull(foundClient);
        assertEquals(client, foundClient);
    }

    @Test
    void shouldNotFoundRentalClient() {
        assertThrows(ClientNotFoundException.class, ()  -> carRentalService.getRentalClientById(9999L));
    }

    @Test
    void shouldAddCarRent() {
        var car1 = addMockCar("Honda","Civic", CarType.SEDAN);
        var client1 = addMockRentalClient("Jessica");
        var rentStartDate = LocalDate.of(2026, 1, 1);
        var rentDays = 3;
        var carRent = carRentalService.rentCar(car1.getId(), client1.getId(), rentStartDate, rentDays);
        var foundRent = carRentRepository.findById(carRent.getId()).orElseThrow();

        assertNotNull(foundRent);
        assertEquals(carRent, foundRent);
    }

    @Test
    void shouldOccurCarRentConflict() {
        var car1 = addMockCar("Chrysler", "Pacifica", CarType.VAN);
        var client1 = addMockRentalClient("Patricia");
        var rentStartDate = LocalDate.of(2026, 2, 10);
        var rentDays = 10;
        var carRent = carRentalService.rentCar(car1.getId(), client1.getId(), rentStartDate, rentDays);
        var foundRent = carRentRepository.findById(carRent.getId()).orElseThrow();
        assertNotNull(foundRent);
        assertEquals(carRent, foundRent);

        var client2 = addMockRentalClient("Donald");
        var rentStartDate2 = LocalDate.of(2026, 2, 15);
        var rentDays2 = 15;
        assertThrows(CarUnavailableException.class, () -> carRentalService.rentCar(car1.getId(), client2.getId(), rentStartDate2, rentDays2));
    }

    @Test
    void concurrentRentalCreation_shouldPreventDoubleBooking() throws InterruptedException {
        var car1 = addMockCar("Chevrolet",  "Tahoe", CarType.SUV);
        var client1 = addMockRentalClient("Margaret");
        var rentStartDate = LocalDate.of(2026, 1, 10);
        var rentDays = 10;

        var client2 = addMockRentalClient("Scott");
        var rentStartDate2 = LocalDate.of(2026, 1, 12);
        var rentDays2 = 5;

        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(2);


        List<CarRental> successfulRentals = new CopyOnWriteArrayList<>();
        List<Exception> exceptions = new CopyOnWriteArrayList<>();

        Runnable task1 = () -> {
            try {
                startLatch.await();
                TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
                txTemplate.execute(status -> {
                    CarRental response = carRentalService.rentCar(car1.getId(), client1.getId(), rentStartDate, rentDays);
                    successfulRentals.add(response);
                    try { Thread.sleep(200); } catch (InterruptedException e) { Thread.currentThread().interrupt(); }

                    return null;
                });
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                endLatch.countDown();
            }
        };

        Runnable task2 = () -> {
            try {
                startLatch.await();
                TransactionTemplate txTemplate = new TransactionTemplate(transactionManager);
                txTemplate.execute(status -> {
                    CarRental response = carRentalService.rentCar(car1.getId(), client2.getId(), rentStartDate2, rentDays2);
                    successfulRentals.add(response);
                    return null;
                });
            } catch (Exception e) {
                exceptions.add(e);
            } finally {
                endLatch.countDown();
            }
        };

        executor.submit(task1);
        executor.submit(task2);

        startLatch.countDown(); // Start!
        endLatch.await(10, TimeUnit.SECONDS);
        executor.shutdown();

        assertThat(successfulRentals).hasSize(1);
        assertThat(exceptions).hasSize(1);

        Exception exception = exceptions.getFirst();
        assertThat(exception).isInstanceOf(OptimisticLockException.class);

        Car car = carRepository.findById(car1.getId()).get();
        assertThat(car.getStatus()).isEqualTo(CarStatus.RENTED);
    }

    private Car addMockCar(String brand, String model, CarType carType) {
        var car = new Car(brand, model, carType);
        return carRepository.save(car);
    }

    private RentalClient addMockRentalClient(String name) {
        var client = new RentalClient(name);
        return rentalClientRepository.save(client);
    }

}
