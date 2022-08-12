package com.saiko.bidmarket.chat.controller.dto;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class ChatMessageSelectRequest {

  @PositiveOrZero
  private final long offset;

  @Positive
  private final int limit;

}
