package com.saiko.bidmarket.chat.controller.dto;

import javax.validation.constraints.Positive;

import org.hibernate.validator.constraints.Length;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PUBLIC)
public class ChatSendMessage {

  @Positive
  private final long userId;

  @Length(min = 1, max = 2000)
  private final String content;

}
