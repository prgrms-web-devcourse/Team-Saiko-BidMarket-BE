package com.saiko.bidmarket.chat.service;

import java.util.List;

import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectRequest;
import com.saiko.bidmarket.chat.controller.dto.ChatMessageSelectResponse;
import com.saiko.bidmarket.chat.controller.dto.ChatPublishMessage;
import com.saiko.bidmarket.chat.service.dto.ChatMessageCreateParam;

public interface ChatMessageService {
  ChatPublishMessage create(ChatMessageCreateParam createParam);

  List<ChatMessageSelectResponse> findAll(long chatRoomId, ChatMessageSelectRequest request);
}
