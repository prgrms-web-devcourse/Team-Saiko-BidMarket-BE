package com.saiko.bidmarket.chat.repository;

import java.util.Optional;

import com.saiko.bidmarket.chat.entity.ChatMessage;

public interface ChatMessageCustomRepository {

  Optional<ChatMessage> findLastChatMessageOfChatRoom(long chatRoom);

}
