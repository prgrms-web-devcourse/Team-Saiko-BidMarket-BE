package com.saiko.bidmarket.chat.service;

import com.saiko.bidmarket.chat.service.dto.ChatRoomCreateParam;

public interface ChatRoomService {

  long create(ChatRoomCreateParam createParam);

}
