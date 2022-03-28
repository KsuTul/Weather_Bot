package com.example.weatherbot.domain;

import java.util.Arrays;

public enum Command {
    START, HELP, WEATHER, SET_CITY, SET_DAYS, NOT_FOUND, CURRENT_WEATHER;

    public static Command valueOfWithIgnoreCase(String command) {
        return Arrays.stream(Command.values()).filter(c -> c.name().equalsIgnoreCase(command)).findAny().orElseThrow(IllegalArgumentException::new);
    }
}
