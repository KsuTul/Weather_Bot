package com.example.weatherbot.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "\"City\"", schema = "schema_name")
public class City {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"city\"")
    private String city;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "\"chatId\"")
    private Chat chat;

    public City(String city){
        this.city = city;
    }

    public City() {

    }
}
