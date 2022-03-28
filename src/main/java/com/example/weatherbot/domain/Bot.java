package com.example.weatherbot.domain;

import com.example.weatherbot.repository.ChatRepository;
import com.example.weatherbot.services.BotService;
import lombok.SneakyThrows;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Component
public class Bot extends TelegramLongPollingBot {

    @Autowired
    private final BotService botService;
    @Autowired
    private ChatRepository chatRepository;

    private final String userName;

    private final String token;

    public Bot(BotService botService, String userName, String token) {
        this.botService = botService;
        this.userName = userName;
        this.token = token;
    }


    @Override
    public String getBotUsername() {
        return userName;
    }

    @Override
    public String getBotToken() {
        return token;
    }

    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        var message = botService.handleMessage(update);

        try{
            execute(message);
        }catch(TelegramApiException ex){
            ex.printStackTrace();
        }

    }

    public void botConnect() throws TelegramApiException {
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            telegramBotsApi.registerBot(this);
        } catch (TelegramApiRequestException e) {
            botConnect();
        }
    }
}
