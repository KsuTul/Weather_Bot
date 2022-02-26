package com.example.weatherbot.domain;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Weather {
    private String mainWeather;
    private Long temp;
    private Long tempFeelsLike;
    private Long tempMin;
    private Long tempMax;
    private LocalDateTime date;
}
