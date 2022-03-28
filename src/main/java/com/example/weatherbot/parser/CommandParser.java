package com.example.weatherbot.parser;

import com.example.weatherbot.domain.Command;
import com.example.weatherbot.services.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Component
public class CommandParser {
    private static final String COMMAND_SIGN = "/";

    @Autowired
    private final WeatherService weatherService;

    public CommandParser(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    public Map<Command, String> getCommand(String text) {
        Command command;
        var result = new HashMap<Command, String>();
        if (text.startsWith(COMMAND_SIGN)) {
            text = text.replaceAll("\\s+", " ").trim();
            var commandText = text.contains(" ") ? text.substring(text.indexOf(COMMAND_SIGN) + 1, text.indexOf(" "))
                    : text.substring(text.indexOf(COMMAND_SIGN) + 1);
            command = Command.valueOfWithIgnoreCase(commandText);
            result.put(command, "");
        }
        if (weatherService.isCityExist(text)) {
            result.put(Command.SET_CITY, text);
        }
        if (isNumber(text)) {
            result.put(Command.SET_DAYS, text);
        }

        return result;
    }

    private boolean isNumber(String text) {
        try {
            Integer.parseInt(text);
        } catch (NumberFormatException ex) {
            return false;
        }

        return true;
    }

}
