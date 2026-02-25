package com.carrental.model;

import com.carrental.enums.CarStatus;
import com.carrental.enums.CarType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(exclude = "id")
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String brand;
    private String model;
    private CarType type;
    private CarStatus status;

    @Version
    private Long version;

    public Car(String brand, String model, CarType carType) {
        this.brand = brand;
        this.model = model;
        this.type = carType;
        this.status = CarStatus.AVAILABLE;
    }
}
