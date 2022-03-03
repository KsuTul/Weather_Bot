package com.example.weatherbot.repository;

import com.example.weatherbot.domain.Chat;
import com.example.weatherbot.domain.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.List;

@Transactional
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("select c from Chat c where c.chatId = :chatId")
    Chat getChatByChatId(Long chatId);

    @Query("select c.city from Chat c where c.chatId = :chatId")
    List<City> getCities(Long chatId);
}