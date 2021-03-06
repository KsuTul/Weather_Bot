package com.example.weatherbot.domain;

import lombok.Data;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "\"Chat\"", schema = "schema_name")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "\"chatId\"")
    private Long chatId;

    @OneToMany(fetch = FetchType.LAZY,  mappedBy = "chat", cascade = CascadeType.MERGE)
    private List<City> city = new ArrayList<>();

    public Chat(){
    }

    public Chat(Long chatId){
        this.chatId = chatId;
        this.city = new ArrayList<>();
    }
}
