package com.carrental.repository;

import com.carrental.model.Car;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

//@Query("select c from Car  where c.carStatus = 'AVAILABLE'")
public interface CarRepository extends JpaRepository<Car, Long> {
}
