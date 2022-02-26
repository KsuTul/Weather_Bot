package com.example.weatherbot;

import com.example.weatherbot.config.BotConfig;
import com.example.weatherbot.domain.Bot;
import org.springframework.boot.configurationprocessor.json.JSONException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;


public class Api {
    public static void main(String[] args) throws TelegramApiException, JSONException, IOException {
        ApplicationContext context = new AnnotationConfigApplicationContext(BotConfig.class);
        var bot = context.getBean(Bot.class);
        bot.botConnect();
    }
}
