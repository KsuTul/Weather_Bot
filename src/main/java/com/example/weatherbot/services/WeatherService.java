package com.example.weatherbot.services;

import com.example.weatherbot.config.WeatherConfig;
import com.example.weatherbot.domain.Weather;
import com.example.weatherbot.domain.WeatherInfo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.log4j.Logger;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Slf4j
@Component
@AllArgsConstructor
@Service
public class WeatherService {

    private final WeatherConfig weatherConfig;

    public WeatherInfo getWeatherFromApi(String city, int day) {
        var result = new StringBuilder();

        var apiConnection = getConnection(city, day);
        if (apiConnection == null) return null;
        try {
            var rd = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        return getWeatherInfo(result);
    }

    public WeatherInfo getCurrentWeatherFromApi(Double latitude, Double longitude) {
        var result = new StringBuilder();

        var apiConnection = getConnection(latitude, longitude);
        if (apiConnection == null) return null;
        try {
            var rd = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();
        } catch (IOException exc) {
            exc.printStackTrace();
        }
        String city;
        try {
            var resultStr = result.toString();
            var jsonObject = new JSONArray(resultStr);
            city = jsonObject.getJSONObject(0).getString("name");
            return getWeatherFromApi(city, 1);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private WeatherInfo getWeatherInfo(StringBuilder result) {
        var weatherInfo = new WeatherInfo();
        try {

            var jsonObject = new JSONObject(result.toString());
            var weatherList = new ArrayList<Weather>();

            weatherInfo.setCity(jsonObject.getJSONObject("city").getString("name"));

            var weatherArray = jsonObject.getJSONArray("list");
            for (var i = 0; i < weatherArray.length(); i++) {
                weatherList.add(getWeatherByTheDay(weatherArray, i));
            }
            weatherInfo.setWeather(weatherList);
            return weatherInfo;

        } catch (JSONException exc) {
            exc.printStackTrace();
        }

        return null;
    }

    public boolean isCityExist(String city) {
        try {
            var connection = getConnection(city, 1);
            if (connection != null) {
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
        } catch (FileNotFoundException ex) {
            return false;
        } catch (IOException e) {
            log.info("Some problem occurred while parsing from weather api");
            e.printStackTrace();
        }
        return true;
    }

    public String getWeatherInfo(Weather weatherInfo) {
        return weatherInfo.getDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")) + " : " + weatherInfo.getTemp() + " ощущается как " + weatherInfo.getTempFeelsLike() + " " + weatherInfo.getMainWeather();
    }

    private URLConnection getConnection(String city, int days) {
        try {
            var url = new URL(MessageFormat.format(weatherConfig.getOpenWeatherUri(), city, weatherConfig.getKey(), days));
            return url.openConnection();
        } catch (IOException e) {
            log.info("Some problem occurred while parsing from weather api");
            e.printStackTrace();
        }

        return null;
    }

    private URLConnection getConnection(Double latitude, Double longitude) {
        try {
            var url = new URL(MessageFormat.format(weatherConfig.getCity(), latitude, longitude, weatherConfig.getKey()));
            return url.openConnection();
        } catch (IOException e) {
            log.info("Some problem occurred while parsing from weather api");
            e.printStackTrace();
        }

        return null;
    }

    private Weather getWeatherByTheDay(JSONArray array, int index){
        var dayOfWeather = new Weather();
        try {
            var obj = array.getJSONObject(index);
            var main = obj.getJSONObject("main");
            dayOfWeather.setTemp(main.getLong("temp"));
            dayOfWeather.setTempFeelsLike(main.getLong("feels_like"));
            dayOfWeather.setTempMin(main.getLong("temp_min"));
            dayOfWeather.setTempMax(main.getLong("temp_max"));
            var mainWeather = obj.getJSONArray("weather").getJSONObject(0);
            dayOfWeather.setMainWeather(mainWeather.getString("main"));
            dayOfWeather.setDate(LocalDateTime.parse(obj.getString("dt_txt"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        } catch (JSONException e) {
            log.info("Error occurred while parsing info");
            e.printStackTrace();
        }

        return dayOfWeather;
    }

}
