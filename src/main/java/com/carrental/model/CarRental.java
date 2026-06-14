package com.carrental.model;

import com.carrental.enums.RentalStatus;
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
public class CarRental {

    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private Car car;

    @ManyToOne
    private RentalClient client;

    private LocalDate startDate;

    private Integer rentDays;

    private LocalDate returnDate;

    @Enumerated(EnumType.STRING)
    private RentalStatus status;

    private LocalDate actualReturnDate;

    @Version
    private Long version;

    public CarRental(Car car, RentalClient client, LocalDate startDate, Integer rentDays, RentalStatus status) {
        this.car = car;
        this.client = client;
        this.rentDays = rentDays;
        this.startDate = startDate;
        this.returnDate = startDate.plusDays(rentDays);
        this.status = status;
    }
}
