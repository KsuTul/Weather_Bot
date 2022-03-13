package com.example.weatherbot.services;

import org.jvnet.hk2.annotations.Service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class CallbackAnswear {

    private static final String URISTR = "https://api.telegram.org/bot{0}/answerCallbackQuery?callback_query_id={1}";

    public void callbackAnswear(String callbackId, Long chatId) {
        var telegramApiClient = HttpClient.newHttpClient();
        var telegramApiClientCallbackAnswearReq = HttpRequest.newBuilder(URI.create(URISTR.replace("{0}", chatId.toString()).replace("{1}", callbackId)))
                .GET().build();

        try {
            telegramApiClient.send(telegramApiClientCallbackAnswearReq, HttpResponse.BodyHandlers.ofString());
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }
}
