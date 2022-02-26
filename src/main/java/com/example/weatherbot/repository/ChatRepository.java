package com.example.weatherbot.repository;

import com.example.weatherbot.domain.Chat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;

@Transactional
@Repository
public interface ChatRepository extends JpaRepository<Chat, Long> {
    @Query("select c from Chat c")
    Chat getChatByChatId();
}