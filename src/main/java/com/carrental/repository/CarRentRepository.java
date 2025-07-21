package com.carrental.repository;

import com.carrental.model.CarRent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CarRentRepository extends JpaRepository<CarRent, Long> {

    @Query("select cr from CarRent cr where cr.car.id = :carId and cr.startDate <= :endDate and cr.expectedReturnDate >= :startDate")
    List<CarRent> findOverlappingCarRent(@Param("carId") Long carId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}
