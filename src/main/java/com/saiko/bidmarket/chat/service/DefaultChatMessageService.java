package com.saiko.bidmarket.chat.service;

import org.springframework.stereotype.Service;

import com.saiko.bidmarket.chat.controller.dto.ChatPublishMessage;
import com.saiko.bidmarket.chat.service.dto.ChatMessageCreateParam;

@Service
public class DefaultChatMessageService implements ChatMessageService {

  @Override
  public ChatPublishMessage create(ChatMessageCreateParam createParam) {
    return null;
  }
}
