package com.carrental.repository;

import com.carrental.model.CarRental;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CarRentalRepository extends JpaRepository<CarRental, Long> {

    @Query("select cr from CarRental cr where cr.car.id = :carId " +
            "and cr.status in ('CONFIRMED', 'ACTIVE') and cr.startDate <= :endDate and cr.returnDate >= :startDate")
    List<CarRental> findOverlappingCarRent(@Param("carId") Long carId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
