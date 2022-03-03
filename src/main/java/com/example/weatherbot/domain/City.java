package com.example.weatherbot.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "City")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "city")
    private String city;

    public City(String city){
        this.city = city;
    }

    public City() {

    }
}
