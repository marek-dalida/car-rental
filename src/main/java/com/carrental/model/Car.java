package com.carrental.model;

import com.carrental.enums.CarStatus;
import com.carrental.enums.CarType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Version;
import lombok.*;

@Entity
@AllArgsConstructor(access = AccessLevel.PROTECTED)
@NoArgsConstructor
@Getter
@EqualsAndHashCode(exclude = "id")
public class Car {

    @Id
    @GeneratedValue
    private Long id;
    private String brand;
    private String model;
    private CarType type;
    @Setter
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
