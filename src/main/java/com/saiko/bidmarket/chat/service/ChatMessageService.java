package com.saiko.bidmarket.chat.service;

import com.saiko.bidmarket.chat.controller.dto.ChatPublishMessage;
import com.saiko.bidmarket.chat.service.dto.ChatMessageCreateParam;

public interface ChatMessageService {
  ChatPublishMessage create(ChatMessageCreateParam createParam);
}
