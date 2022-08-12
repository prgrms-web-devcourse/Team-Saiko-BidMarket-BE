package com.saiko.bidmarket.chat.repository;

import java.util.List;
import java.util.Optional;

import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectRequest;
import com.saiko.bidmarket.chat.entity.ChatMessage;

public interface ChatMessageCustomRepository {

  Optional<ChatMessage> findLastChatMessageOfChatRoom(long chatRoom);

  List<ChatMessage> findAllChatMessage(
      long chatRoomId,
      ChatMessageSelectRequest request
  );

}
