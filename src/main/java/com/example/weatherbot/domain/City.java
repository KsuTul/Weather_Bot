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

    @ManyToOne
    @JoinColumn(name = "\"chatId\"", insertable = false, updatable = false)
    private Chat chat;

    public City(String city, Chat chat){
        this.city = city;
        this.chat = chat;
    }

    public City() {

    }
}
