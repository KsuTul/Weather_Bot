package com.example.weatherbot.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class WeatherInfo {
    private String city;
    private List<Weather> weather = new ArrayList<>();
}
