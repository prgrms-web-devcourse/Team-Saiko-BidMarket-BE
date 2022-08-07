package com.saiko.bidmarket.chat.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import com.saiko.bidmarket.chat.controller.dto.ChatPublishMessage;
import com.saiko.bidmarket.chat.controller.dto.ChatSendMessage;
import com.saiko.bidmarket.chat.service.ChatMessageService;
import com.saiko.bidmarket.chat.service.dto.ChatMessageCreateParam;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

@Validated
@Controller
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ChatWebSocketController {

  private final Logger log = LoggerFactory.getLogger(getClass());

  private final ChatMessageService chatService;

  @MessageMapping("/room/{id}")
  @SendTo("/chat/room/{id}")
  public ChatPublishMessage send(@DestinationVariable long id,
                                 @Valid ChatSendMessage chatSendMessage) {
    log.info("Chat Message | room : {}, user : {}, content : {}", id, chatSendMessage.getUserId(),
             chatSendMessage.getContent());

    ChatMessageCreateParam param = ChatMessageCreateParam.of(id, chatSendMessage);

    return chatService.create(param);
  }
}
