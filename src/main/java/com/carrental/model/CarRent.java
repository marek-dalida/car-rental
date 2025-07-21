package com.carrental.model;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
public class CarRent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Car car;

    @ManyToOne
    private RentalClient client;

    private LocalDate startDate;

    private Integer rentDays;

    // todo: return date could be different than expected return date
    private LocalDate returnDate;

    private LocalDate expectedReturnDate;

    public CarRent(Car car, RentalClient client, LocalDate startDate, Integer rentDays) {
        this.car = car;
        this.client = client;
        this.rentDays = rentDays;
        this.startDate = startDate;
        this.expectedReturnDate = startDate.plusDays(rentDays);
    }
}
