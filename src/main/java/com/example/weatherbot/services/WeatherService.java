package com.example.weatherbot.services;

import com.example.weatherbot.domain.Weather;
import com.example.weatherbot.domain.WeatherInfo;
import lombok.AllArgsConstructor;
import org.apache.log4j.Logger;
import org.springframework.boot.configurationprocessor.json.JSONArray;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.boot.configurationprocessor.json.JSONObject;
import org.springframework.stereotype.Component;

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

@Component
@AllArgsConstructor
public class WeatherService {
    private static final String OPEN_WEATHER_URI = "http://api.openweathermap.org/data/2.5/forecast?q={0}&appid=e2589c81147cb1fa05c55af330714b95&units=metric&cnt={1}";
    private final Logger logger;

    public WeatherInfo getWeatherFromApi(String city, int day) {
        var result = new StringBuilder();
        try{
            var apiConnection = getConnection(city, day);
            if(apiConnection == null) return null;
            var rd = new BufferedReader(new InputStreamReader(apiConnection.getInputStream()));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            rd.close();

            var weatherInfo = new WeatherInfo();
            var jsonObject = new JSONObject(result.toString());
            var weatherList = new ArrayList<Weather>();
            weatherInfo.setCity(jsonObject.getJSONObject("city").getString("name"));

            var weatherArray = jsonObject.getJSONArray("list");
            for (var i = 0; i < weatherArray.length(); i++) {
                weatherList.add(getWeatherByTheDay(weatherArray, i));
            }
            weatherInfo.setWeather(weatherList);
            return weatherInfo;
        } catch(IOException ex){
           logger.info( "Error occurred while reading from file " + ex);
           return null;
        } catch (JSONException e) {
            logger.info("Error occurred while parsing info");
            e.printStackTrace();
        }
        return null;
    }

    public  boolean isCityExist(String city) {
        try {
            var connection = getConnection(city, 1);
            if(connection != null){
                new BufferedReader(new InputStreamReader(connection.getInputStream()));
            }
        } catch(FileNotFoundException ex){
            return false;
        } catch (IOException e) {
            logger.info("Some problem occurred while parsing from weather api");
            e.printStackTrace();
        }
        return true;
    }

    private URLConnection getConnection(String city, int days){
        try {
           var url = new URL(MessageFormat.format(OPEN_WEATHER_URI, city, days));
            return url.openConnection();
        } catch (IOException e) {
            logger.info("Some problem occurred while parsing from weather api");
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
            logger.info("Error occurred while parsing info");
            e.printStackTrace();
        }

        return dayOfWeather;
    }

}
