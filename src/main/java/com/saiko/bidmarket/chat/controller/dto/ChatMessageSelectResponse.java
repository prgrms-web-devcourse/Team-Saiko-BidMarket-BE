package com.saiko.bidmarket.chat.controller.dto;

import java.time.LocalDateTime;

import com.saiko.bidmarket.chat.entity.ChatMessage;
import com.saiko.bidmarket.user.entity.User;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ChatMessageSelectResponse {

  private final ChatUserInfo userInfo;
  private final String content;
  private final LocalDateTime createdAt;

  public static ChatMessageSelectResponse from(ChatMessage chatMessage) {
    User sender = chatMessage.getSender();

    return ChatMessageSelectResponse
        .builder()
        .userInfo(ChatUserInfo.from(sender))
        .content(chatMessage.getMessage())
        .createdAt(chatMessage.getCreatedAt())
        .build();
  }
}
