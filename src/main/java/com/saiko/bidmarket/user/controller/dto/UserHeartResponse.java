package com.saiko.bidmarket.user.controller.dto;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UserHeartResponse {
  private final boolean heart;

  public static UserHeartResponse from(boolean heart) {
    return new UserHeartResponse(heart);
  }
}
