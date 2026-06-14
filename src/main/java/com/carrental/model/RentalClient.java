package com.carrental.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@EqualsAndHashCode(exclude = "id")
public class RentalClient {

    @Id
    @GeneratedValue
    private Long id;

    @Column(unique = true)
    private String name;

    public RentalClient(String name) {
        this.name = name;
    }
}
