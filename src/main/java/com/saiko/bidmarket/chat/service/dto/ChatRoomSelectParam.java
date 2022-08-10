package com.saiko.bidmarket.chat.service.dto;

import static lombok.AccessLevel.*;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import com.saiko.bidmarket.chat.controller.dto.ChatRoomSelectRequest;

import lombok.Builder;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@Builder(access = PRIVATE)
@RequiredArgsConstructor(access = PRIVATE)
public class ChatRoomSelectParam {

  @Positive
  private final long userId;

  @PositiveOrZero
  private final long offset;

  @Positive
  private final int limit;

  public static ChatRoomSelectParam of(
      long userId,
      ChatRoomSelectRequest request
  ) {
    return ChatRoomSelectParam
        .builder()
        .limit(request.getLimit())
        .offset(request.getOffset())
        .userId(userId)
        .build();
  }

}
