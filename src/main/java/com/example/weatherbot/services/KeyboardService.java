package com.example.weatherbot.services;

import com.example.weatherbot.repository.ChatRepository;
import com.example.weatherbot.repository.CityRepository;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@Transactional
public class KeyboardService {

    private final InlineKeyboardMarkup keyboard;
    private final WeatherService weatherService;

    @Autowired
    private ChatRepository chatRepository;

    public KeyboardService(InlineKeyboardMarkup keyboard, WeatherService weatherService) {
        this.keyboard = keyboard;
        this.weatherService = weatherService;
    }

    public InlineKeyboardMarkup setChooseCityKeyboard(Long chatId){
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        var citiesByChat = chatRepository.getCities(chatId);
        if(citiesByChat.isPresent()){
            for (var city:
                    citiesByChat.get().getCity()) {
                var button = new InlineKeyboardButton();
                button.setText(city.getCity());
                var weather = weatherService.getWeatherFromApi(city.getCity(), 1);
                if(weather.getWeather().size() > 0){
                    button.setCallbackData(weatherService.getWeatherInfo(weather.getWeather().get(0)));
                }
                keyboardRow.add(button);
            }
        }

        var addCityButton  = new InlineKeyboardButton();
        addCityButton.setText("Добавить новый город");
        addCityButton.setCallbackData("Введите новый город");
        keyboardRow.add(addCityButton);

        keyboard.setKeyboard(List.of(keyboardRow));

        return keyboard;
    }
}
