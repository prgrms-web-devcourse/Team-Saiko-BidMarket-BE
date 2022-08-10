package com.saiko.bidmarket.chat.service;

import java.util.List;

import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectRequest;
import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectResponse;
import com.saiko.bidmarket.chat.service.dto.ChatRoomCreateParam;

public interface ChatRoomService {

  long create(ChatRoomCreateParam createParam);

  List<ChatRoomSelectResponse> findAll(
      long userId,
      ChatRoomSelectRequest request
  );

}
