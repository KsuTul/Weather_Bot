package com.example.weatherbot.services;


import com.example.weatherbot.domain.Chat;
import com.example.weatherbot.domain.City;
import com.example.weatherbot.parser.CommandParser;
import com.example.weatherbot.repository.ChatRepository;
import com.example.weatherbot.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

@Service
public class BotService {
    @Autowired
    private final WeatherService weatherService;
    private final CommandParser commandParser;
    private final KeyboardService keyboardService;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private ChatRepository chatRepository;

    private String active;

    public BotService(WeatherService weatherService, CommandParser parser, KeyboardService keyboardService) {
        this.weatherService = weatherService;
        this.commandParser = parser;
        this.keyboardService = keyboardService;
    }

    public SendMessage handleMessage(Update update) {
        SendMessage message = new SendMessage();

        if (update.hasCallbackQuery()) {
            message = new SendMessage();
            message.setText(update.getCallbackQuery().getData());
            message.setChatId(String.valueOf(update.getCallbackQuery().getMessage().getChatId()));
            return message;
        }

        var chatId = update.getMessage().getChatId();
        var chat = chatRepository.getChatByChatId(chatId);
        message.setChatId(String.valueOf(chatId));

        if (chat == null) {
            chatRepository.save(new Chat(chatId));
        }
        if (update.getMessage().getLocation() != null) {
            var weather = weatherService.getCurrentWeatherFromApi(
                    update.getMessage().getLocation().getLatitude(), update.getMessage().getLocation().getLongitude());
            if (weather.getWeather().size() > 0) {
                message.setText(weatherService.getWeatherInfo(weather.getWeather().get(0)));
            }
            return message;
        }
        var text = update.getMessage().getText();
        var commands = commandParser.getCommand(text);


        for (var command : commands.entrySet()) {
            switch (command.getKey()) {
                case WEATHER:
                    message.setText("Выберете город");
                    message.setReplyMarkup(getCityKeyboard(chatId));
                    break;
                case SET_CITY:
                    System.out.println("The command - set_city");
                    message.setText(setCity(command.getValue(), chatId));
                    break;
                case SET_DAYS:
                    setDays(Integer.parseInt(command.getValue()));
                    break;
                case CURRENT_WEATHER:
                    message.setText("Введите геолокацию");
                    break;

            }
        }
        return message;
    }

    private InlineKeyboardMarkup getCityKeyboard(Long chatId) {
        return keyboardService.setChooseCityKeyboard(chatId);
    }

    private String setCity(String city, Long chatId) {
        var cities = chatRepository.getCities(chatId);
        if (cities.isEmpty()) {
            return "Проверьте правильность написания города " + city;
        }
        var cityOpt = cities.get().getCity().stream().filter(c -> c.getCity().equalsIgnoreCase(city)).findFirst();
        if (weatherService.isCityExist(city) && cityOpt.isEmpty()) {
            var chat = chatRepository.getChatByChatId(chatId);
            var newCity = new City(city, chat);
            cityRepository.save(newCity);
            var weather = weatherService.getWeatherFromApi(city, 1);
            if (weather.getWeather().size() > 0) {
                return weatherService.getWeatherInfo(weather.getWeather().get(0));
            }
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
                result.append(weatherService.getWeatherInfo(weatherByDay));
            }

            return result.toString();
        }
        return "Ошибка в введенных данных";
    }


}
