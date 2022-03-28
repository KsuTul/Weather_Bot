package com.example.weatherbot.config;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Data
@Component
public class WeatherConfig {
    @Value("${openWeather.key}")
    private String key;

    @Value("${openWeather.uri}")
    private String openWeatherUri;

    @Value("${openWeather.city}")
    private String city;

}
