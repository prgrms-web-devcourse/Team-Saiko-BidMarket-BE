package com.saiko.bidmarket.chat.repository;

import java.util.List;

import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectRequest;
import com.saiko.bidmarket.chat.entity.ChatRoom;

public interface ChatRoomCustomRepository {
  List<ChatRoom> findAllByUserId(long userId, ChatRoomSelectRequest request);
}
