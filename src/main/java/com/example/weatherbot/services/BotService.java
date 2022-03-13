package com.example.weatherbot.services;


import com.example.weatherbot.domain.City;
import com.example.weatherbot.parser.CommandParser;
import com.example.weatherbot.repository.ChatRepository;
import com.example.weatherbot.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import java.util.ArrayList;
import java.util.List;

@Service
public class BotService {
    private final WeatherService weatherService;
    private final CommandParser commandParser;
    private final KeyboardService keyboardService;

    @Autowired
    private CityRepository cityRepository;

    private String active;

    public BotService(WeatherService weatherService, CommandParser parser, KeyboardService keyboardService) {
        this.weatherService = weatherService;
        this.commandParser = parser;
        this.keyboardService = keyboardService;
    }

    public SendMessage handleMessage(String text, Long chatId) {
        var commands = commandParser.getCommand(text);
        SendMessage message = new SendMessage();
        for (var command : commands.entrySet()) {
            switch (command.getKey()) {
                case WEATHER:
                    message.setText("Выберете город");
                    message.setReplyMarkup(weather(chatId));
                case CITY:
                   // return "Введите город";
                case SET_CITY:
                   // return setCity(command.getValue());
                case DAYS:
                   // return "Введите количество дней";
                case SET_DAYS:
                   // return setDays(Integer.parseInt(command.getValue()));

            }
        }
        return message;
     //   return "Не понял команды";
    }

    // будет функционал кнопок
    private InlineKeyboardMarkup weather(Long chatId) {
        return keyboardService.setChooseCityKeyboard(chatId) ;
    }

    private String setCity(String city) {
        if (weatherService.isCityExist(city)) {
            cityRepository.save(new City(city));
            return "Добавлен новый город в коллекцию " + city;
        }
        return "Проверьте правильность написания города " + city;
    }

    private String setDays(int days) {
        if (days > 0) {
            var weather = weatherService.getWeatherFromApi(active, days);
            StringBuilder result = new StringBuilder();
            var weathers = weather.getWeather();
            for (var weatherByDay :
                    weathers) {
                var answear = weatherByDay.getDate() + " : " + weatherByDay.getTemp() + " ощущается как " + weatherByDay.getTempFeelsLike() + " " + weatherByDay.getMainWeather();
                result.append(answear);
            }

            return result.toString();
        }
        return "Ошибка в введенных данных";
    }
}
