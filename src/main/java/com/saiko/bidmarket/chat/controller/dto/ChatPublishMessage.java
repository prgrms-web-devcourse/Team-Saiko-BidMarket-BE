package com.saiko.bidmarket.chat.controller.dto;

import java.time.LocalDateTime;

import org.springframework.util.Assert;

import com.saiko.bidmarket.chat.entity.ChatMessage;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatPublishMessage {

  private long userId;

  private String content;

  private LocalDateTime createdAt;

  public static ChatPublishMessage of(ChatMessage chatMessage) {
    Assert.notNull(chatMessage, "ChatMessage must be provided");

    return ChatPublishMessage.builder()
                             .userId(chatMessage.getSender().getId())
                             .content(chatMessage.getMessage())
                             .createdAt(chatMessage.getCreatedAt())
                             .build();
  }
}

