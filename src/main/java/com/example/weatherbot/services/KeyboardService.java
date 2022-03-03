package com.example.weatherbot.services;

import com.example.weatherbot.repository.ChatRepository;
import com.example.weatherbot.repository.CityRepository;
import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class KeyboardService {

    private final InlineKeyboardMarkup keyboard;

    @Autowired
    private ChatRepository chatRepository;

    public KeyboardService(InlineKeyboardMarkup keyboard) {
        this.keyboard = keyboard;
    }

    public InlineKeyboardMarkup setChooseCityKeyboard(Long chatId){
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        var citiesByChat = chatRepository.getCities(chatId);
        for (var city:
             citiesByChat) {
            var button = new InlineKeyboardButton();
            button.setText(city.getCity());
            button.setCallbackData("Сейчас" + city);
            keyboardRow.add(button);
        }

        var addCityButton  = new InlineKeyboardButton();
        addCityButton.setText("Добавить новый город");
        addCityButton.setCallbackData("Введите новый город");
        keyboardRow.add(addCityButton);

        keyboard.setKeyboard(List.of(keyboardRow));

        return keyboard;
    }
}
