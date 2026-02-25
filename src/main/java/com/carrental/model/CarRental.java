package com.carrental.model;

import com.carrental.enums.RentalStatus;
import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
public class CarRental {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    public CarRental(Car car, RentalClient client, LocalDate startDate, Integer rentDays, RentalStatus status) {
        this.car = car;
        this.client = client;
        this.rentDays = rentDays;
        this.startDate = startDate;
        this.returnDate = startDate.plusDays(rentDays);
        this.status = status;
    }
}
