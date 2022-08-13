package com.saiko.bidmarket.chat.controller.dto;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import com.saiko.bidmarket.chat.entity.ChatMessage;

import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Getter
@Builder
@Jacksonized
public class ChatPublishMessage {

  private final ChatUserInfo chatUserInfo;
  private final String content;
  private final LocalDateTime createdAt;

  public static ChatPublishMessage of(ChatMessage chatMessage) {
    Assert.notNull(chatMessage, "ChatMessage must be provided");

    ChatUserInfo chatUserInfo = ChatUserInfo.from(chatMessage.getSender());
    return ChatPublishMessage
        .builder()
        .chatUserInfo(chatUserInfo)
        .content(chatMessage.getMessage())
        .createdAt(chatMessage.getCreatedAt())
        .build();
  }
}
