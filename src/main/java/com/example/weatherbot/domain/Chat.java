package com.example.weatherbot.domain;

import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "Chat")
public class Chat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "ChatId")
    private Long chatId;

//    @OneToMany(fetch = FetchType.LAZY)
//    @Column(name = "CityId")
//    private List<City> city;

    public Chat(){
    }

    public Chat(Long chatId){
        this.chatId = chatId;
        //this.city = new ArrayList<>();
    }
}
