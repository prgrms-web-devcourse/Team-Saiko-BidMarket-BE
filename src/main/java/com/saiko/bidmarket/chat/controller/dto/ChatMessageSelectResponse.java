package com.saiko.bidmarket.chat.controller.dto;

import java.time.LocalDateTime;

import com.saiko.bidmarket.chat.entity.ChatMessage;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
public class ChatMessageSelectResponse {

  private final long userId;
  private final String content;
  private final LocalDateTime createdAt;

  public static ChatMessageSelectResponse of(ChatMessage chatMessage) {
    return ChatMessageSelectResponse
        .builder()
        .userId(chatMessage.getSender().getId())
        .content(chatMessage.getMessage())
        .createdAt(chatMessage.getCreatedAt())
        .build();
  }

}
