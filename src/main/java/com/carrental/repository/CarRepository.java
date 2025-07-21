package com.carrental.repository;

import com.carrental.model.Car;
import jakarta.persistence.Entity;
import org.springframework.data.jpa.repository.JpaRepository;


public interface CarRepository extends JpaRepository<Car, Long> {
}
