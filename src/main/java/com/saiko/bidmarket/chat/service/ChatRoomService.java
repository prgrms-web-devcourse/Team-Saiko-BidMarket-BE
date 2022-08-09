package com.saiko.bidmarket.chat.service;

import java.util.List;

import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectResponse;
import com.saiko.bidmarket.chat.service.dto.ChatRoomCreateParam;
import com.saiko.bidmarket.chat.service.dto.ChatRoomSelectParam;

public interface ChatRoomService {

  long create(ChatRoomCreateParam createParam);

  List<ChatRoomSelectResponse> findAll(ChatRoomSelectParam selectParam);

}
