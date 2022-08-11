package com.saiko.bidmarket.chat.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.saiko.bidmarket.chat.entity.ChatMessage;

@Repository
public interface ChatMessageRepository
    extends ChatMessageCustomRepository, JpaRepository<ChatMessage, Long> {
}
