package com.saiko.bidmarket.chat.service.dto;

import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;

import com.saiko.bidmarket.chat.controller.dto.ChatSendMessage;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = AccessLevel.PRIVATE)
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class ChatMessageCreateParam {

  @Positive
  private final long userId;

  @Positive
  private final long roomId;

  @Length(min = 1, max = 2000)
  private final String content;

  public static ChatMessageCreateParam of(long roomId, ChatSendMessage sendMessage) {
    return ChatMessageCreateParam.builder()
                                 .roomId(roomId)
                                 .userId(sendMessage.getUserId())
                                 .content(sendMessage.getContent())
                                 .build();
  }
}
