package com.carrental.repository;

import com.carrental.model.RentalClient;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RentalClientRepository extends JpaRepository<RentalClient, Long> {
}
